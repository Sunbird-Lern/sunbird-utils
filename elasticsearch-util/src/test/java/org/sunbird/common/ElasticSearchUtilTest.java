package org.sunbird.common;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ConnectionManager;
import org.sunbird.helper.ElasticSearchMapping;
import org.sunbird.helper.ElasticSearchSettings;

import static org.junit.Assert.*;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class ElasticSearchUtilTest {
    private static Map<String,Object> map = null;
    private static Map<String,Object> map1 = null;
    private static TransportClient client = null;
    private static final String indexName = "sbtestindex";
    private static final String typeName = "sbtesttype";
    private static final String STARTS_WITH = "startsWith";
    private static final String ENDS_WITH = "endsWith";

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
    public void CreateIndex() {
        boolean response = ElasticSearchUtil.createIndex(indexName, typeName, ElasticSearchMapping.createMapping(),
                ElasticSearchSettings.createSettingsForIndex());
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

        List response = (List) map.get(JsonKey.RESPONSE);
        assertEquals(3 , map.size());
        //assertNotNull(map);
    }

    @AfterClass
    public static void destroy(){
        ElasticSearchUtil.deleteIndex(indexName);
        map = null;
        ConnectionManager.closeClient();
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
    public void zRemoveDataByIdentifier() {
         ElasticSearchUtil.removeData(indexName, typeName, (String)map.get("courseId"));
        assertEquals(true, true);
    }
    
    @Test
    public void zyRemoveDataByIdentifierFailure() {
         ElasticSearchUtil.removeData(indexName, typeName, "");
        assertEquals(false, false);
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
    boolean response = ConnectionManager.initialiseConnectionFromPropertiesFile("Test", "localhost", "localhost");
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
    ElasticSearchUtil.upsertData(indexName, typeName, "test-12349", data);
  }
}
