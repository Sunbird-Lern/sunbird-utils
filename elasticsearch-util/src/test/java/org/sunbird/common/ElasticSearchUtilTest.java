package org.sunbird.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.common.ElasticSearchUtil;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ConnectionManager;
import org.sunbird.helper.ConnectionManager.ResourceCleanUp;
import org.sunbird.helper.ElasticSearchMapping;
import org.sunbird.helper.ElasticSearchSettings;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ElasticSearchUtilTest {
    private static Map<String,Object> map = null;
    private static Map<String,Object> map1 = null;
    private static TransportClient client = null;
    private static final String indexName = "sbtestindex";
    private static final String typeName = "sbtesttype";
    private static final String STARTS_WITH = "startsWith";
    private static final String ENDS_WITH = "endsWith";
    private static long startTime = System.currentTimeMillis();
    @BeforeClass
    public static void init() {
        map = intitialiseMap(5);
        map1 = intitialiseMap1(60);
        client = ConnectionManager.getClient();
    }

    @Test
    public void testConnectionSuccess() {
        client = ConnectionManager.getClient();
        assertNotNull(client);
    }

    @Test
    public void acreateIndex() {
        boolean response = ElasticSearchUtil.createIndex(indexName, typeName, ElasticSearchMapping.createMapping(),
                ElasticSearchSettings.createSettingsForIndex());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ProjectLogger.log("Index creation time out");
        }
        assertTrue(response);
    }

    @Test
    public void createDataTest() {
        String responseId = ElasticSearchUtil.createData(indexName, typeName, (String)map.get("courseId"), map);
        //inserting second record
        ElasticSearchUtil.createData(indexName, typeName, (String)map1.get("courseId"), map1);
        assertEquals(responseId, map.get("courseId"));
        //wait for 1 second since the created data will reflect after some time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getByIdentifier() {
        Map<String,Object> responseMap = ElasticSearchUtil.getDataByIdentifier(indexName, typeName, (String)map.get("courseId"));
        assertEquals(responseMap.get("courseId"), map.get("courseId"));
    }


    @Test
    public void updateByIdentifier() {
        Map<String,Object> innermap = new HashMap<>();
        innermap.put("courseName", "updatedCourese name");
        innermap.put(   "organisationId","updatedOrgId");
        boolean response = ElasticSearchUtil.updateData(indexName, typeName, (String)map.get("courseId"), innermap);
        assertTrue(response);
    }


    @Test
    public void updateByIdentifierTest() {
        Map<String, Object> responseMap = ElasticSearchUtil.getDataByIdentifier(indexName, typeName,
                (String) map.get("courseId"));
        assertEquals(responseMap.get("courseName"), "updatedCourese name");
    }

    @Test
    public void testComplexSearch(){
        SearchDTO searchDTO = new SearchDTO();
        List<String> fields = new ArrayList<String>();
        fields.add("courseId");
        fields.add("courseType");
        fields.add("createdOn");
        fields.add("description");
        List<String> excludefields = new ArrayList<String>();
        excludefields.add("createdOn");
        Map<String , String> sortMap = new HashMap<>();
        sortMap.put("courseType" , "ASC");
        searchDTO.setSortBy(sortMap);
        searchDTO.setExcludedFields(excludefields);
        searchDTO.setLimit(20);
        searchDTO.setOffset(0);
        Map<String,Object> additionalPro = new HashMap<String, Object>();
        searchDTO.addAdditionalProperty("test", additionalPro);
        Map<String , Object> additionalProperties = new HashMap<String , Object>();

        List<String> existsList = new ArrayList<String>();
        existsList.add("pkgVersion");
        existsList.add("size");

        additionalProperties.put(JsonKey.EXISTS , existsList);
        List<String> description = new ArrayList<String>();
        description.add("This is for chemistry");
        description.add("Hindi Jii");
        List<Integer> sizes = new ArrayList<Integer>();
        sizes.add(10);
        sizes.add(20);
        Map<String , Object> filterMap = new HashMap<String , Object>();
        filterMap.put("description" , description);
        filterMap.put("size" , sizes);
        additionalProperties.put(JsonKey.FILTERS,filterMap);
        Map<String, Object> rangeMap = new HashMap<String , Object>();
        rangeMap.put(">",0);
        filterMap.put("pkgVersion" , rangeMap);
        Map<String , Object> lexicalMap = new HashMap<>();
        lexicalMap.put(STARTS_WITH , "type");
        filterMap.put("courseType" , lexicalMap);
        Map<String , Object> lexicalMap1 = new HashMap<>();
        lexicalMap1.put(ENDS_WITH , "sunbird");
        filterMap.put("courseAddedByName" , lexicalMap1);
        //for exact math key value pair
        filterMap.put("orgName" , "Name of the organisation");

        searchDTO.setAdditionalProperties(additionalProperties);
        searchDTO.setFields(fields);
        searchDTO.setQuery("organisation");
        //facets
        List<Map<String,String>> facets = new ArrayList<>();
        Map<String , String> m1 = new HashMap<>();
        m1.put("description" , null);
        m1.put("createdOn", JsonKey.DATE_HISTOGRAM);
        facets.add(m1);
        searchDTO.setFacets(facets);

        //soft constraints
        List<String> mode = Arrays.asList("soft");
        searchDTO.setMode(mode);
        Map<String , Integer> constraintMap = new HashMap<String , Integer>();
        constraintMap.put("grades" , 10);
        constraintMap.put("pkgVersion" , 5);
        searchDTO.setSoftConstraints(constraintMap);
        searchDTO.setQuery("organisation Name published");


        Map map = ElasticSearchUtil.complexSearch(searchDTO,indexName , typeName);

        assertEquals(3 , map.size());
    }

    
    @Test
    public void testComplexSearchWithRangeGreater(){
        SearchDTO searchDTO = new SearchDTO();
        Map<String , Object> additionalProperties = new HashMap<String , Object>();
        List<Integer> sizes = new ArrayList<Integer>();
        sizes.add(10);
        sizes.add(20);
        Map<String , Object> filterMap = new HashMap<String , Object>();
        filterMap.put("size" , sizes);
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put("createdOn", "2017-11-06");
        filterMap.put(">=", innerMap);
        additionalProperties.put(JsonKey.FILTERS,filterMap);
        Map<String, Object> rangeMap = new HashMap<String , Object>();
        rangeMap.put(">",0);
        filterMap.put("pkgVersion" , rangeMap);
        Map<String , Object> lexicalMap = new HashMap<>();
        lexicalMap.put(STARTS_WITH , "type");
        filterMap.put("courseType" , lexicalMap);
        Map<String , Object> lexicalMap1 = new HashMap<>();
        lexicalMap1.put(ENDS_WITH , "sunbird");
        filterMap.put("courseAddedByName" , lexicalMap1);
        //for exact math key value pair
        filterMap.put("orgName" , "Name of the organisation");

        searchDTO.setAdditionalProperties(additionalProperties);
        searchDTO.setQuery("organisation");
        Map map = ElasticSearchUtil.complexSearch(searchDTO,indexName , typeName);
        assertEquals(2 , map.size());
    }

   
    @Test
    public void testComplexSearchWithRangeLessThan(){
        SearchDTO searchDTO = new SearchDTO();
        Map<String , Object> additionalProperties = new HashMap<String , Object>();
        List<Integer> sizes = new ArrayList<Integer>();
        sizes.add(10);
        sizes.add(20);
        Map<String , Object> filterMap = new HashMap<String , Object>();
        filterMap.put("size" , sizes);
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put("createdOn", "2017-11-06");
        filterMap.put("<=", innerMap);
        additionalProperties.put(JsonKey.FILTERS,filterMap);
        Map<String, Object> rangeMap = new HashMap<String , Object>();
        rangeMap.put(">",0);
        filterMap.put("pkgVersion" , rangeMap);
        Map<String , Object> lexicalMap = new HashMap<>();
        lexicalMap.put(STARTS_WITH , "type");
        filterMap.put("courseType" , lexicalMap);
        Map<String , Object> lexicalMap1 = new HashMap<>();
        lexicalMap1.put(ENDS_WITH , "sunbird");
        filterMap.put("courseAddedByName" , lexicalMap1);
        //for exact math key value pair
        filterMap.put("orgName" , "Name of the organisation");

        searchDTO.setAdditionalProperties(additionalProperties);
        searchDTO.setQuery("organisation");
        Map map = ElasticSearchUtil.complexSearch(searchDTO,indexName , typeName);
        assertEquals(2 , map.size());
    }
    
    @Test
    public void getByIdentifierWithOutIndex() {
        Map<String,Object> responseMap = ElasticSearchUtil.getDataByIdentifier(null, typeName, (String)map.get("courseId"));
        assertEquals(0, responseMap.size());
    }
   
    @Test
    public void getByIdentifierWithOutType() {
        Map<String,Object> responseMap = ElasticSearchUtil.getDataByIdentifier(indexName, null, "testcourse123");
        assertEquals(0, responseMap.size());
    }
    
    @Test
    public void getByIdentifierWithOutTypeIndexIdentifier() {
        Map<String,Object> responseMap = ElasticSearchUtil.getDataByIdentifier(null, null, "");
        assertEquals(0, responseMap.size());
    }
    
    
    @Test
    public void getDataWithOutIdentifier() {
        Map<String,Object> responseMap = ElasticSearchUtil.getDataByIdentifier(indexName, typeName, "");
        assertEquals(0, responseMap.size());
    }
    
    @Test
    public void updateDataWithOutIdentifier() {
        Map<String,Object> innermap = new HashMap<>();
        innermap.put("courseName", "updatedCourese name");
        innermap.put(   "organisationId","updatedOrgId");
        boolean response = ElasticSearchUtil.updateData(indexName, typeName, null, innermap);
        assertFalse(response);
    }   
    
    
    @Test
    public void updateWithEmptyMap() {
        Map<String,Object> innermap = new HashMap<>();
        boolean response = ElasticSearchUtil.updateData(indexName, typeName, (String)map.get("courseId"), innermap);
        assertFalse(response);
    }   
    
    @Test
    public void updateWithNullMap() {
        boolean response = ElasticSearchUtil.updateData(indexName, typeName, (String)map.get("courseId"), null);
        assertFalse(response);
    }  
    
    @Test
    public void upsertDataWithOutIdentifier() {
        Map<String,Object> innermap = new HashMap<>();
        innermap.put("courseName", "updatedCourese name");
        innermap.put(   "organisationId","updatedOrgId");
        boolean response = ElasticSearchUtil.upsertData(indexName, typeName, null, innermap);
        assertFalse(response);
    }   
    
    
    @Test
    public void upsertDataWithOutIndex() {
        Map<String,Object> innermap = new HashMap<>();
        innermap.put("courseName", "updatedCourese name");
        innermap.put(   "organisationId","updatedOrgId");
        boolean response = ElasticSearchUtil.upsertData(null, typeName, (String)map.get("courseId"), innermap);
        assertFalse(response);
    }  
    
    @Test
    public void upsertDataWithOutIndexType() {
        Map<String,Object> innermap = new HashMap<>();
        innermap.put("courseName", "updatedCourese name");
        innermap.put(   "organisationId","updatedOrgId");
        boolean response = ElasticSearchUtil.upsertData(null, null, (String)map.get("courseId"), innermap);
        assertFalse(response);
    }  
    
    @Test
    public void upsertDataWithEmptyMap() {
        Map<String,Object> innermap = new HashMap<>();
        boolean response = ElasticSearchUtil.upsertData(indexName, typeName, (String)map.get("courseId"), innermap);
        assertFalse(response);
    }  
    
    
    @Test
    public void createIndexWithEmptyIndexName() {
        boolean response = ElasticSearchUtil.createIndex("", typeName, ElasticSearchMapping.createMapping(),
                ElasticSearchSettings.createSettingsForIndex());
        assertFalse(response);
    }
    
    
    @Test
    public void createIndexWithEmptyIndexNameAndType() {
        boolean response = ElasticSearchUtil.createIndex("", null, ElasticSearchMapping.createMapping(),
                ElasticSearchSettings.createSettingsForIndex());
        assertFalse(response);
    }
    
    @Test
    public void createIndexWithEmptyMapping() {
        boolean response = ElasticSearchUtil.createIndex(indexName, typeName, null,
                ElasticSearchSettings.createSettingsForIndex());
        assertFalse(response);
    }
    
    @Test
    public void createIndexWithEmptySettings() {
        boolean response = ElasticSearchUtil.createIndex(indexName, typeName, ElasticSearchMapping.createMapping(),
                null);
        assertFalse(response);
    }
    
    @AfterClass
    public static void destroy(){
        ElasticSearchUtil.deleteIndex(indexName);
        map = null;
        ConnectionManager.closeClient();
       ConnectionManager.ResourceCleanUp clean = new ResourceCleanUp();
       clean.start();
    }



    private static Map<String,Object> intitialiseMap(int val) {
        Map<String,Object> map = new HashMap<>();
        map.put("courseType","type of the course. all , private");
        map.put("description", "This is for chemistry");
        map.put( "size",10);
        map.put("objectType","course");
        map.put("courseId","course id_"+val);
        map.put( "courseName","NTP course_"+val);
        map.put(  "courseDuration",val);
        map.put(  "noOfLecture",30+val);
        map.put(   "organisationId","org id");
        map.put(   "orgName","Name of the organisation");
        map.put("courseAddedById","who added the course in NTP");
        map.put( "courseAddedByName","Name of the person who added the course under sunbird");
        map.put(  "coursePublishedById","who published the course");
        map.put(  "coursePublishedByName","who published the course");
        map.put(  "enrollementStartDate",new Date());
        map.put(  "publishedDate",new Date());
        map.put(  "updatedDate",new Date());
        map.put(   "updatedById","last updated by id");
        map.put("updatedByName","last updated person name");

        map.put("facultyId","faculty for this course");
        map.put( "facultyName","name of the faculty");
        map.put( "CoursecontentType","list of course content type as comma separated , pdf, video, wordDoc");
        map.put(  "availableFor","[\"C.B.S.C\",\"I.C.S.C\",\"all\"]");
        map.put(  "tutor","[{\"id\":\"name\"},{\"id\":\"name\"}]");
        map.put(   "operationType","add/updated/delete");
        map.put(    "owner","EkStep");
        //map.put("identifier","do_112228048362078208130");
        map.put( "visibility","Default");
        map.put(  "downloadUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905653021_do_112228048362078208130_5.0.ecar");

        map.put("language", "[\"Hindi\"]");
        map.put("mediaType","content");
        map.put("variants",  "{\"spine\": {\"ecarUrl\": \"https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905655272_do_112228048362078208130_5.0_spine.ecar\",\"size\": 863}}");
        map.put("mimeType", "application/vnd.ekstep.html-archive");
        map.put("osId", "org.ekstep.quiz.app");
        map.put("languageCode","hi");
        map.put("createdOn","2017-05-04T13:47:32.676+0000");
        map.put("pkgVersion", val);
        map.put("versionKey","1495646809112");

        map.put( "lastPublishedOn","2017-05-04T13:47:33.000+0000");
        map.put("collections","[{\"identifier\": \"do_1121912573615472641169\",\"name\": \"A\",\"objectType\": \"Content\",\"relation\": \"hasSequenceMember\",\"description\": \"A.\",\"index\": null}]");
        map.put( "name","Test Content 1");
        map.put("artifactUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/content/do_112228048362078208130/artifact/advancedenglishassessment1_1533_1489654074_1489653812104_1492681721669.zip");
        map.put( "lastUpdatedOn", "2017-05-24T17:26:49.112+0000");
        map.put("contentType","Story");
        map.put("status","Live");
        map.put("channel" , "NTP");
        return map;
    }

    private static Map<String,Object> intitialiseMap1(int val) {
        Map<String,Object> map = new HashMap<>();
        map.put("courseType","type of the course. all , private");
        map.put("description", "This is for physics");
        map.put( "size",20);
        map.put("objectType","course");
        map.put("courseId","course id_"+val);
        map.put( "courseName","NTP course_"+val);
        map.put(  "courseDuration",val);
        map.put(  "noOfLecture",30+val);
        map.put(   "organisationId","org id");
        map.put(   "orgName","Name of the organisation");
        map.put("courseAddedById","who added the course in NTP");
        map.put( "courseAddedByName","Name of the person who added the course under sunbird");
        map.put(  "coursePublishedById","who published the course");
        map.put(  "coursePublishedByName","who published the course");
        map.put(  "enrollementStartDate",new Date());
        map.put(  "publishedDate",new Date());
        map.put(  "updatedDate",new Date());
        map.put(   "updatedById","last updated by id");
        map.put("updatedByName","last updated person name");

        map.put("facultyId","faculty for this course");
        map.put( "facultyName","name of the faculty");
        map.put( "CoursecontentType","list of course content type as comma separated , pdf, video, wordDoc");
        map.put(  "availableFor","[\"C.B.S.C\",\"I.C.S.C\",\"all\"]");
        map.put(  "tutor","[{\"id\":\"name\"},{\"id\":\"name\"}]");
        map.put(   "operationType","add/updated/delete");
        map.put(    "owner","EkStep");
        //map.put("identifier","do_112228048362078208130");
        map.put( "visibility","Default");
        map.put(  "downloadUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905653021_do_112228048362078208130_5.0.ecar");

        map.put("language", "[\"Hindi\"]");
        map.put("mediaType","content");
        map.put("variants",  "{\"spine\": {\"ecarUrl\": \"https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905655272_do_112228048362078208130_5.0_spine.ecar\",\"size\": 863}}");
        map.put("mimeType", "application/vnd.ekstep.html-archive");
        map.put("osId", "org.ekstep.quiz.app");
        map.put("languageCode","hi");
        map.put("createdOn","2017-06-04T13:47:32.676+0000");
        map.put("pkgVersion", val);
        map.put("versionKey","1495646809112");

        map.put( "lastPublishedOn","2017-05-04T13:47:33.000+0000");
        map.put("collections","[{\"identifier\": \"do_1121912573615472641169\",\"name\": \"A\",\"objectType\": \"Content\",\"relation\": \"hasSequenceMember\",\"description\": \"A.\",\"index\": null}]");
        map.put( "name","Test Content 1");
        map.put("artifactUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/content/do_112228048362078208130/artifact/advancedenglishassessment1_1533_1489654074_1489653812104_1492681721669.zip");
        map.put( "lastUpdatedOn", "2017-05-24T17:26:49.112+0000");
        map.put("contentType","Story");
        map.put("status","Live");
        map.put("channel" , "NTP");
        return map;
    }
    
    @Test
    public void saveDataWithOutIndexname() {
        String responseMap = ElasticSearchUtil.createData("", typeName, (String)map.get("courseId"),map);
        assertEquals("ERROR", responseMap);
    }
    
    @Test
    public void saveDataWithOutTypeName() {
        String responseMap = ElasticSearchUtil.createData(indexName, "", (String)map.get("courseId"),map);
        assertEquals("ERROR", responseMap);
    }
   
    @Test
    public void getDataByEmptyIdentifier() {
        Map<String,Object> responseMap = ElasticSearchUtil.getDataByIdentifier(indexName, typeName, "");
        assertEquals(0, responseMap.size());
    }
   
  
    @Test
    public void searchDtoGetQueryTest() {
        SearchDTO dto = new SearchDTO();
        List<Map<String,Object>> list = new ArrayList<>();  
        Map<String,Object>  map = new HashMap<>();
        map.put("name", "test1");
        list.add(map);
        dto.setGroupQuery(list);
       list =  dto.getGroupQuery();
       assertEquals(1, list.size());
       dto.setOperation("add");
       assertEquals("add", dto.getOperation());
       dto.setFuzzySearch(true);
       assertTrue(dto.isFuzzySearch());
    }
    
    @Test
    public void searchDtoArgumentedConst() {
        List<Map> list = new ArrayList<>();  
        Map  map = new HashMap();
        map.put("name", "test1");
        list.add(map);
        SearchDTO dto = new SearchDTO(list,"add",5);
        assertEquals(1,  dto.getProperties().size());
        map.put("city", "mycity");
        list.add(map);
        dto.setProperties(list);
        assertEquals(2, dto.getProperties().get(0).size());
    }
    
    @Test
    public void zRemoveDataByIdentifier() {
        boolean response = ElasticSearchUtil.removeData(indexName, typeName, (String)map.get("courseId"));
        assertEquals(true, response);
    }
    
    @Test
    public void zyRemoveDataByIdentifierFailure() {
        boolean response = ElasticSearchUtil.removeData(indexName, typeName, "");
        assertEquals(false, response);
    }
    
   @Test 
   public void checkMappingObject () {
     ElasticSearchMapping mapping = new ElasticSearchMapping();
     assertTrue(mapping != null);
   } 
   
   @Test 
   public void checkSettingsObject () {
     ElasticSearchSettings settings = new ElasticSearchSettings();
     assertTrue(settings != null);
   } 
   
   //@Test
   public void zzyTestWhenClusterisNull () {
     ElasticSearchUtil.deleteIndex(indexName);
     ConnectionManager.closeClient();
     ConnectionManager.getClient();
   }
   
   @Test 
   public void failureConnectionTestFromProperties () {
    boolean response = ConnectionManager.initialiseConnectionFromPropertiesFile("Test", "localhost1,128.0.0.1", "9200,9300");
    assertFalse(response);
   }
   
   @Test 
  public void checkHealth() {
    boolean response = false;
    try {
      response = ElasticSearchUtil.healthCheck();
    } catch (InterruptedException e) {

    } catch (ExecutionException e) {
    }
    assertEquals(true,response);
  }
   
  @Test
  public void upsertDataTest () {
    Map<String,Object> data = new HashMap<String, Object>();
    data.put("test", "test");
    boolean response = ElasticSearchUtil.upsertData(indexName, typeName, "test-12349", data);
    assertEquals(true,response);
  }
  
  @Test
  public void bulkInsertTest () {
    Map<String,Object> data = new HashMap<String, Object>();
    data.put("test1", "test");
    data.put("test2", "manzarul");
    List<Map<String,Object>> listOfMap = new ArrayList<Map<String,Object>>();
    listOfMap.add(data);
    boolean response = ElasticSearchUtil.bulkInsertData(indexName, typeName, listOfMap);
   assertEquals(true,response);
  }
  
  @Test
  public void search () {
    Map<String,Object> data = new HashMap<String, Object>();
    data.put("test1", "test");
    try {
    Map<String,Object> map = ElasticSearchUtil.searchData(indexName, typeName, data);
     assertTrue(map != null);
     assertTrue(map.size()==0);
    } catch (Exception e) {
    }
  }
  @Test
  public void mappingAddOrUpdateTest () {
    boolean bool = ElasticSearchUtil.addOrUpdateMapping(indexName, typeName, ElasticSearchMapping.createMapping());
    assertTrue(bool);
  }
  
  @Test
  public void searchMetricsDataTestSuccess(){
    String index = "searchindex";
    String type = "user";
    String rawQuery = "{\"query\":{\"match_none\":{}}}";
    Response response = ElasticSearchUtil.searchMetricsData(index, type, rawQuery);
    assertEquals(ResponseCode.OK, response.getResponseCode());
  }
  
  @Test
  public void searchMetricsDataTestException(){
    String index = "searchtest";
    String type = "usertest";
    String rawQuery = "{\"query\":{\"match_none\":{}}}";
    try {
    ElasticSearchUtil.searchMetricsData(index, type, rawQuery);
    }catch(ProjectCommonException e){
      assertEquals(ResponseCode.unableToConnectToES.getErrorCode(), e.getCode());
    }
  }
  
  public void testcalculateEndTime() {
	   long endTime = ElasticSearchUtil.calculateEndTime(startTime);
	   assertTrue(endTime>startTime);
  }
}
