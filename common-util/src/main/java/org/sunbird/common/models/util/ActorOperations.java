package org.sunbird.common.models.util;
/**
 * This enum will contains different operation
 *  for a learner {addCourse, getCourse, update , getContent}
 * @author Manzarul
 *
 */
public enum ActorOperations {
	ENROLL_COURSE("enrollCourse"),GET_COURSE("getCourse"),ADD_CONTENT("addContent"),
	GET_CONTENT("getContent"),CREATE_COURSE("createCourse"),UPDATE_COURSE("updateCourse"),PUBLISH_COURSE("publishCourse"),SEARCH_COURSE("searchCourse")
	,DELETE_COURSE("deleteCourse"),CREATE_USER("createUser"),UPDATE_USER("updateUser"),LOGIN("login"),LOGOUT("logout"),CHANGE_PASSWORD("changePassword"),
	USER_AUTH("userAuth"),GET_PROFILE("getUserProfile");

	private String value;

	/**
	 * constructor
	 * @param value
	 */
	ActorOperations(String value){
		this.value=value;
	}

	/**
	 * returns the enum value
	 * @return
	 */
	public String getValue(){
		return this.value;
	}
}
