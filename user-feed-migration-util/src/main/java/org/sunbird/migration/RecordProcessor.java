package org.sunbird.migration;

import com.datastax.driver.core.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.sunbird.migration.connection.Connection;
import org.sunbird.migration.connection.factory.ConnectionFactory;
import org.sunbird.migration.tracker.RecordTracker;
import org.sunbird.migration.tracker.StatusTracker;

public class RecordProcessor extends StatusTracker {

  private ConnectionFactory connectionFactory;
  public Connection connection;
  private RequestParams requestParams;
  private static Logger logger = LoggerFactory.getLoggerInstance(RecordProcessor.class.getName());

  /**
   * constructor for the class
   *
   * @param connectionFactory
   * @param requestParams
   */
  private RecordProcessor(ConnectionFactory connectionFactory, RequestParams requestParams) {
    this.connectionFactory = connectionFactory;
    this.requestParams = requestParams;
    this.connection =
        connectionFactory.getConnection(
            requestParams.getCassandraHost(),
            requestParams.getCassandraKeyspaceName(),
            requestParams.getCassandraPort());
  }



  /**
   * this method should be used to get the instance of the class..
   *
   * @param connectionFactory
   * @param requestParams
   * @return
   */
  public static RecordProcessor getInstance(
      ConnectionFactory connectionFactory, RequestParams requestParams) {
    return new RecordProcessor(connectionFactory, requestParams);
  }

  /**
   * this method is responsible to fetch the user feed data from cassandra
   *
   * @return List<Feed>
   */
  public List<Feed> getFeedLists() {
    ResultSet resultSet = connection.getRecords("select * from sunbird.user_feed");
    List<Feed> feedList = CassandraHelper.getFeedFromResultSet(resultSet);
    logger.debug(feedList.iterator().next());
    return feedList;
  }

  /**
   * this method will be used to process externalId this method will get the count of total number
   * of records that need to be processed
   *
   * @return
   */
  public void startProcessing(List<Feed> feeds) throws IOException {

    int size = feeds.size();
    int count1[] = new int[1];
    getRecordsToBeProcessed(feeds);
    /** List of user feeds */
    logger.info("Total User Feed Records: " + size);

    logger.info(
        "==================Starting Processing User Feed=======================================");
    feeds
        .stream()
        .forEach(
            feed -> {
              try {
                startTracingUserRecord(feed.getId());
                boolean isSuccess =
                    performSequentialOperationOnUserFeedRecord(feed);
                if (isSuccess) {
                  count1[0] += 1;
                  logUserFeedUpdateSuccessQuery(feed.getId());
                }
              } catch (Exception ex) {
                logUpdateFailedRecord(feed.getId());
              } finally {
                endTracingUserRecord(feed.getId());
              }
            });
    logger.info(
        "==================Ending Processing User Feed=======================================");
    closeFailedWriterConnection();
    closeSuccessWriterConnection();
    connection.closeConnection();


  }

  private boolean performSequentialOperationOnUserFeedRecord(Feed feed) {
    try {
      FeedV2 feedV2 = transformV1toV2Notification(feed);
      FeedV2 feedV1 = transformV1Format(feed);
      List<Map<String, Object>> feedList = new ArrayList<>();
      List<FeedV2> feedV2List = new ArrayList<>();
      feedV2List.add(feedV1);
      feedV2List.add(feedV2);
      ObjectMapper mapper = new ObjectMapper();
      feedList = mapper.convertValue(feedV2List, new TypeReference<List<Map<String, Object>>>() {
      });
      ResultSet resultSet = null;
      if (CollectionUtils.isNotEmpty(feedList)) {
        BatchStatement batchStatement = CassandraHelper.createBatchStatement(feedList);
        Session session = connection.getSession();
        resultSet = session.execute(batchStatement);
        return true;
      }
    }catch (Exception ex){
      return false;
    }
    return false;
  }

  /**
     * this methods get the count of records from cassandra as totalUserFeedList then this method will
   * remove the preprocessed records from totalUserFeedList.
   *
   * @return List<String>
   * @throws IOException
   */
  private void getRecordsToBeProcessed(List<Feed> feeds) throws IOException {
    logger.info("total records in db is " + feeds.size());
    List<String> preProcessedRecords = RecordTracker.getUserFeedPreProcessedRecordsAsList();
    logger.info("total records found preprocessed is " + preProcessedRecords.size());
    removePreProcessedRecordFromList(preProcessedRecords, feeds);
  }

  private FeedV2 transformV1Format(Feed feed) {
    FeedV2 feed1  = new FeedV2();
    feed1.setId(feed.getId());
    feed1.setCategory(feed.getCategory());
    feed1.setStatus(feed.getStatus());
    feed1.setExpireon(feed.getExpireOn());
    feed1.setCreatedby(feed.getCreatedBy());
    feed1.setCreatedon(feed.getCreatedOn());
    feed1.setUpdatedby(feed.getUpdatedBy());
    feed1.setUpdatedon(new Timestamp(Calendar.getInstance().getTime().getTime()));
    feed1.setUserid(feed.getUserId());
    feed1.setAction(feed.getData());
    feed1.setVersion("v1");
    return feed1;
  }


  private FeedV2 transformV1toV2Notification(Feed notification) throws IOException {

    FeedV2 feedV2 = new FeedV2();
    feedV2.setId(UUID.randomUUID().toString());
    feedV2.setCategory(notification.getCategory());
    feedV2.setStatus(notification.getStatus());
    feedV2.setExpireon(notification.getExpireOn());
    feedV2.setCreatedby(notification.getCreatedBy());
    feedV2.setCreatedon(notification.getCreatedOn());
    feedV2.setUpdatedby(notification.getUpdatedBy());
    feedV2.setUpdatedon(new Timestamp(Calendar.getInstance().getTime().getTime()));
    feedV2.setUserid(notification.getUserId());
    Map<String,Object> actionMap = new HashMap<>();
    String data = notification.getData();
    ObjectMapper mapper = new ObjectMapper();
    Map<String,Object> map = mapper.readValue(data,Map.class);
    Map<String,Object> createdBy = new HashMap<>();
    createdBy.put("type","System");
    createdBy.put("id",notification.getCreatedBy());
    Map<String,Object> template = new HashMap<>();
    template.put("type","JSON");
    template.put("ver","4.4");
    Map<String,String> dataTemplate = new HashMap<>();
    Map<String,Object> additionalInfo = new HashMap<>();
    for (Map.Entry<String,Object> itr: map.entrySet()) {
         if(itr.getKey().equals("actionData")){
           Map<String,Object> actionData = ( Map<String,Object>)itr.getValue();
           for (Map.Entry<String,Object> itr1: actionData.entrySet()) {
             if(itr1.getKey().equals("title") || itr1.getKey().equals("description")){
               dataTemplate.put(itr1.getKey(), (String) itr1.getValue());
             }else{
               additionalInfo.put(itr1.getKey(),itr1.getValue());
             }
           }

         }else{
           additionalInfo.put(itr.getKey(),itr.getValue());
         }
    }
    actionMap.put("type",additionalInfo.get("actionType"));
    actionMap.put("category",notification.getCategory());
    template.put("data",mapper.writeValueAsString(dataTemplate));
    actionMap.put("template",template);
    actionMap.put("createdBy",createdBy);
    actionMap.put("additionalInfo",additionalInfo);
    feedV2.setAction(mapper.writeValueAsString(actionMap));
    return feedV2;
  }
  /**
   * this methods
   *
   * @param preProcessedRecords
   * @param totalRecords
   * @return
   */
  private List<Feed> removePreProcessedRecordFromList(
          List<String> preProcessedRecords, List<Feed> totalRecords) {
    totalRecords.removeIf(
            feed -> (preProcessedRecords.contains(String.format("%s", feed.getId()))));
    logTotalRecords(totalRecords.size());
    return totalRecords;
  }

}
