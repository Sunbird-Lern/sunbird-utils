/**
 * 
 */
package org.sunbird.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.cluster.snapshots.status.TransportNodesSnapshotsStatus.Request;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.sunbird.common.ElasticSearchUtil;

/**
 * This class will create elastic search query
 * from requested search data. 
 * @author Manzarul
 *
 */
public class ElasticSearchQueryBuilder {
	
	public static void createIndex(String indexName,String typeName) {
	    TransportClient client	= ConnectionManager.getClient();
	    try {
			boolean response = client.admin().indices().exists(Requests.indicesExistsRequest(indexName)).get().isExists();
			TypesExistsRequest typesExistsRequest = new TypesExistsRequest(new String[]{indexName},typeName);
			boolean typeResponse = client.admin().indices().typesExists(typesExistsRequest).get().isExists();
			System.out.println("found==" + response +"  "+typeResponse);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   //CreateIndexResponse response = client.admin().indices().prepareCreate(indexName).get();
	   PutMappingResponse response  = client.admin().indices().preparePutMapping(indexName).setType(typeName).setSource(createMapping()).get();
	   System.out.println(response.isAcknowledged());
	}
	
	
	public static void main(String[] args) {
		//createIndex("sunbird", "course");
		 //ElasticSearchUtil.createIndex("sunbird-inx5", "course", createMapping(), createSettingsForIndex());
		//ElasticSearchUtil.deleteIndex("sunbird-inx4");
		//createIndex("sunbird-inx3", "course");
		//for (int i=5;i<10;i++)
		//ElasticSearchUtil.createData("sunbird-inx5", "course", "NTP course id_"+i,createMapData(i));
		  Map<String, Object> map = ElasticSearchUtil.getDataByIdentifier("sunbird", "course", "0122636962555002880");
		 System.out.println(map!=null?map.get("courseName"):"Not found");
		 //ElasticSearchUtil.removeData("sunbird-inx3", "course", "NTP course id_70");
		//ElasticSearchUtil.updateData("sunbird-inx3", "course", "NTP course id_71", createMapData(30));
		 //ElasticSearchUtil.searchData("sunbird-inx3", "course", createMapData(30));
	}
	
	
	public static String createMapping () {
		String mapping = 
				" { \"dynamic_templates\": [{\"longs\": { \"match_mapping_type\": \"long\",\"mapping\": {\"type\": \"long\",\"fields\": { \"raw\": {\"type\": \"long\" }}}} },{\"booleans\": {\"match_mapping_type\": \"boolean\",\"mapping\": {\"type\": \"boolean\",\"fields\": {\"raw\": {\"type\": \"boolean\"}}}}},{\"doubles\": {\"match_mapping_type\": \"double\",\"mapping\": {\"type\": \"double\",\"fields\": {\"raw\": { \"type\": \"double\"}}}}},{\"dates\": {\"match_mapping_type\": \"date\",\"mapping\": {\"type\": \"date\",\"fields\": {\"raw\": {\"type\": \"date\"}}}} },{\"strings\": {\"match_mapping_type\": \"string\",\"mapping\": {\"type\": \"string\",\"copy_to\": \"all_fields\",\"fields\": {\"raw\": {\"type\": \"string\",\"analyzer\": \"standard\"}}}}}],\"properties\": {\"all_fields\": {\"type\": \"string\",\"fields\": {\"raw\": {\"type\": \"string\",\"analyzer\": \"standard\"}}}}}";
		return mapping;
	}
	
	
	public static String createSettingsForIndex () {
		String settings = "{\"analysis\": {\"analyzer\": {\"cs_index_analyzer\": {\"type\": \"custom\",\"tokenizer\": \"standard\",\"filter\": [\"lowercase\",\"mynGram\"]},\"cs_search_analyzer\": {\"type\": \"custom\",\"tokenizer\": \"standard\",\"filter\": [\"standard\",\"lowercase\"]},\"keylower\": {\"tokenizer\": \"keyword\",\"filter\": \"lowercase\"}},\"filter\": {\"mynGram\": {\"type\": \"ngram\",\"min_gram\": 1,\"max_gram\": 20,\"token_chars\": [\"letter\", \"digit\",\"whitespace\",\"punctuation\",\"symbol\"]} }}}";
		return settings;
	}
	
	/**
	 * 
	 * @return
	 */
	private static Map<String,Object> createMapData(int val) {
		Map<String,Object> map = new HashMap<>();
		//objectType,identifier, changes createdFor to availableFor
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
		map.put("courseType","type of the course. all , private");
		map.put("facultyId","faculty for this course");
		map.put( "facultyName","name of the faculty");
		map.put( "CoursecontentType","list of course content type as comma separated , pdf, video, wordDoc");  
		map.put(  "availableFor","[\"C.B.S.C\",\"I.C.S.C\",\"all\"]");
		map.put(  "tutor","[{\"id\":\"name\"},{\"id\":\"name\"}]");
		map.put(   "operationType","add/updated/delete");
		map.put(    "owner","EkStep");
		map.put("identifier","do_112228048362078208130");
		map.put( "visibility","Default");
		map.put(  "downloadUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905653021_do_112228048362078208130_5.0.ecar");
		map.put("description", "Test Content 1");
		map.put("language", "[\"Hindi\"]");
		map.put("mediaType","content");
		map.put("variants",  "{\"spine\": {\"ecarUrl\": \"https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905655272_do_112228048362078208130_5.0_spine.ecar\",\"size\": 863}}");
		map.put("mimeType", "application/vnd.ekstep.html-archive");
		map.put("osId", "org.ekstep.quiz.app");
		map.put("languageCode","hi");
		map.put("createdOn","2017-05-04T13:47:32.676+0000");
		map.put("pkgVersion", 5);
		map.put("versionKey","1495646809112");
		map.put( "size",2699766);
		map.put( "lastPublishedOn","2017-05-04T13:47:33.000+0000");
		map.put("collections","[{\"identifier\": \"do_1121912573615472641169\",\"name\": \"A\",\"objectType\": \"Content\",\"relation\": \"hasSequenceMember\",\"description\": \"A.\",\"index\": null}]");
		map.put( "name","Test Content 1");
		map.put("artifactUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/content/do_112228048362078208130/artifact/advancedenglishassessment1_1533_1489654074_1489653812104_1492681721669.zip");
		map.put( "lastUpdatedOn", "2017-05-24T17:26:49.112+0000");
		map.put("contentType","Story");
		map.put("status","Live");
		return map;
	} 
	
	
	
}
