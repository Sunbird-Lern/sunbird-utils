cassandra version 3.9.0

open cassandra.yaml file

change line

authenticator: AllowAllAuthenticator

with

authenticator: PasswordAuthenticator.



CREATE KEYSPACE IF NOT EXISTS sunbird WITH replication = {'class':'SimpleStrategy','replication_factor':3};
to change cluster name
UPDATE system.local SET cluster_name = 'sunbird' where key='local';
ALTER USER cassandra WITH PASSWORD 'password';
USE sunbird;

//creation of id= one way hash of (userId##courseId) here courseId is identifier of course mgmt table
//toc url we have to generate through json of content id from ekStep 
//here status is (default(0),inProgress(1),completed(2))
//progress is no of content completed
CREATE TABLE IF NOT EXISTS sunbird.course_enrollment(id text, courseId text, courseName text,userId text,enrolledDate text,
description text,tocUrl text,status int,active boolean,delta text,grade text,progress int,lastReadContentId text,
lastReadContentStatus int,addedBy text,courseLogoUrl text,dateTime timestamp,PRIMARY KEY (id));

CREATE INDEX inx_ce_userId ON sunbird.course_enrollment (userId);
CREATE INDEX inx_ce_courseId ON sunbird.course_enrollment (courseId);
CREATE INDEX inx_ce_course_name ON sunbird.course_enrollment (courseName);
CREATE INDEX inx_ce_status ON sunbird.course_enrollment (status);


//creation of id = one way hash of (userId##contentId##courseId) 
//status is (default(0),inProgress(1),completed(2))
CREATE TABLE IF NOT EXISTS sunbird.content_consumption(id text, contentId text, courseId text, userId text,viewPosition text,viewCount int,lastAccessTime text,
contentVersion text,completedCount int,status int,result text,score text,grade text,lastUpdatedTime text,lastCompletedTime text,dateTime timestamp,PRIMARY KEY (id));

CREATE INDEX inx_cc_userId ON sunbird.content_consumption (userId);
CREATE INDEX inx_cc_contentId ON sunbird.content_consumption (contentId);
CREATE INDEX inx_cc_status ON sunbird.content_consumption (status);
CREATE INDEX inx_cc_courseId ON sunbird.content_consumption (courseId);


//creation of id = using timestamp and env 
// id and courseId both are same
//content id is from ekstep 
//status DRAFT("draft"), LIVE("live"), RETIRED("retired")
//contentType (pdf,video,word doc etc)
//tutor   map<id,name>
        
CREATE TABLE IF NOT EXISTS sunbird.course_management(id text, courseId text, contentId text, courseName text,courseType text,
facultyId text,facultyName text,organisationId text,organisationName text,enrollementStartDate text,enrollementEndDate text,
courseDuration text,description text,status text,addedBy text,addedByName text,publishedBy text,publishedByName text,createdDate text,
publishedDate text,updatedDate text,updatedBy text,updatedByName text,contentType text,createdfor list<text>,noOfLectures text,
tutor map<text,text>,courseLogoUrl text,PRIMARY KEY (id));

CREATE INDEX inx_cm_facultyId ON sunbird.course_management (facultyId);
CREATE INDEX inx_cm_organisationId ON sunbird.course_management (organisationId);
CREATE INDEX inx_cm_courseId ON sunbird.course_management (courseId);
CREATE INDEX inx_cm_course_name ON sunbird.course_management (courseName);
CREATE INDEX inx_cm_status ON sunbird.course_management (status);
CREATE INDEX inx_cm_contentId ON sunbird.course_management (contentId);

//creation of id = one way hash of userName
//here id and userId both are same
// currently username and email is same
//email and username is unique
CREATE TABLE IF NOT EXISTS  sunbird.user(id text,userId text,userName text, email text,phone text,aadhaarNo text,createdDate text,updatedDate text,updatedBy text,
lastLoginTime text,status int,firstName text,lastName text,password text,avatar text,gender text,language text,state text,city text,zipcode text,PRIMARY KEY (id));

CREATE INDEX inx_u_email ON sunbird.user (email);
CREATE INDEX inx_u_phone ON sunbird.user (phone);
CREATE INDEX inx_u_status ON sunbird.user (status);
CREATE INDEX inx_u_userId ON sunbird.user (userId);
CREATE INDEX inx_u_userName ON sunbird.user (userName);

//id is auth token
CREATE TABLE IF NOT EXISTS sunbird.user_auth(id text, userId text,createdDate text,updatedDate text,source text,PRIMARY KEY (id));
CREATE INDEX inx_ua_userId ON sunbird.user_auth (userId);
CREATE INDEX inx_ua_source ON sunbird.user_auth (source);

CREATE TABLE IF NOT EXISTS sunbird.organisation(id text, orgName text, description text,communityId text,createdBy text,createdByName text,createdDate text,
updatedDate text,updatedBy text,status int,relation text,parentOrgId text,orgType text,state text,city text,zipcode text,dateTime timestamp,PRIMARY KEY (id));

CREATE INDEX inx_org_orgName ON sunbird.organisation (orgName);
CREATE INDEX inx_org_status ON sunbird.organisation (status);

id=  using timestamp and env 
CREATE TABLE IF NOT EXISTS sunbird.page_management(id text, name text, appMap Map<text,int>,portalMap Map<text,int>,createdDate text,createdBy text,updatedDate text,updatedBy text,PRIMARY KEY (id));
CREATE INDEX inx_pm_pageName ON sunbird.page_management (name);

id=  using timestamp and env 
CREATE TABLE IF NOT EXISTS sunbird.page_section(id text, name text, sectionDataType text,description text,display text,
searchQuery text,createdDate text,createdBy text,updatedDate text,updatedBy text,imgUrl text,alt text,status int,PRIMARY KEY (id));
CREATE INDEX inx_ps_sectionDataType ON sunbird.page_section (sectionDataType);
CREATE INDEX inx_ps_sectionName ON sunbird.page_section (name);



