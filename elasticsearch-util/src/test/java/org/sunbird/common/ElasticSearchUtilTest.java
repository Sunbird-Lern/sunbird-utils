package org.sunbird.common;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class ElasticSearchUtilTest {
	
	@BeforeClass 
	public static void init() {
		//TODO cook the data for test 
	}
	
	@Test
	public void createDataTest() {
		assertEquals("SUCCESS", "SUCCESS");
	}
    
	
	@AfterClass
	public static void destroy{
		//TODO remove all the inserted/ updated data
	}
}
