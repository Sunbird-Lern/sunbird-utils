package org.sunbird.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectUtil;

public class App {

	public static void main(String[] args) {
		CassandraConnectionManager.createConnection("127.0.0.1", "9042", "cassandra", "password", "cassandraKeySpace");
		CassandraOperation cassandraOperation= new CassandraOperationImpl();
		
		/*id text, courseId text, courseName text,facultyId text,facultyName text,organisationId text,
		 * organisationName text,enrollementStartDate text,enrollementEndDate text,courseDuration float,
		 * description text,status text,addedById text,addedByName text,publishedById text,publishedByName text,
		 * createdDate text,publishedDate text,updatedDate text,updatedById text,updatedByName text,version text,
		 * createdfor list<text>,tutor map<text,text>,PRIMARY KEY (id)*/
		Map<String, Object> map = new HashMap<>();
		map.put("id", "cd1");
		map.put("courseId", "course1");
		map.put("courseName", "Math");
		map.put("facultyId", "fid1");
		map.put("facultyName", "arvind");
		map.put("organisationId", "ek Step1");
		map.put("organisationName", "ek step");
		map.put("enrollementStartDate", ProjectUtil.getFormattedDate());
		map.put("enrollementEndDate", ProjectUtil.getFormattedDate());
		map.put("courseDuration", 2.5f);
		map.put("description", "description");
		map.put("status", "status");
		map.put("addedById", "addedById");
		map.put("addedByName", "addedByName");
		map.put("publishedById", "publishedById");
		map.put("publishedByName", "publishedByName");
		List<String> list = new ArrayList<>();
		list.add("createdfor1");
		list.add("createdfor2");
		list.add("createdfor3");
		list.add("createdfor4");
		map.put("createdfor", list);
		Map<String, String> tutorMap = new HashMap<>();
		tutorMap.put("tutorId1", "tutor  name1");
		tutorMap.put("tutorId2", "tutor  name2");
		tutorMap.put("tutorId3", "tutor  name3");
		tutorMap.put("tutorId4", "tutor  name4");
		map.put("tutor", tutorMap);
		map.put("createdDate", ProjectUtil.getFormattedDate());
		map.put("publishedDate", ProjectUtil.getFormattedDate());
		map.put("version", "v1.0");
		//Response result = cassandraOperation.insertRecord("cassandraKeySpace","course_management",map);
		
		Response result1 = cassandraOperation.getRecordById("cassandraKeySpace","course_management", "cd1");
		//insertRecord("cassandraKeySpace","course_management",map);
		cassandraOperation.updateRecord("cassandraKeySpace","course_management",map);
System.out.println(result1);
	}

}
