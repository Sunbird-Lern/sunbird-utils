open cassandra.yaml file

change line

authenticator: AllowAllAuthenticator

with

authenticator: PasswordAuthenticator.



CREATE KEYSPACE IF NOT EXISTS cassandraKeySpace WITH replication = {'class':'SimpleStrategy','replication_factor':3};
to change cluster name
UPDATE system.local SET cluster_name = 'sunbird' where key='local';
ALTER USER cassandra WITH PASSWORD 'password';
USE cassandraKeySpace;

//creation of id= one way hash of (userId##courseId) here courseId is identifier of course mgmt table
//toc url we have to generate through json of content id from ekStep 
//here status is (default(0),inProgress(1),completed(2))
//progress is no of content completed
CREATE TABLE IF NOT EXISTS cassandraKeySpace.course_enrollment(id text, courseId text, courseName text,userId text,enrolledDate text,description text,tocUrl text,status int,
active boolean,delta text,grade text,progress text,lastReadContentId text,lastReadContentStatus int,dateTime timestamp,addedBy text,PRIMARY KEY (id));

CREATE INDEX inx_ce_userId ON cassandraKeySpace.course_enrollment (userId);
CREATE INDEX inx_ce_courseId ON cassandraKeySpace.course_enrollment (courseId);
CREATE INDEX inx_ce_course_name ON cassandraKeySpace.course_enrollment (courseName);
CREATE INDEX inx_ce_status ON cassandraKeySpace.course_enrollment (status);


//creation of id = one way hash of (userId##contentId##courseId) 
//status is (default(0),inProgress(1),completed(2))
CREATE TABLE IF NOT EXISTS cassandraKeySpace.content_consumption(id text, contentId text, courseId text, userId text,viewPosition text,viewCount int,lastAccessTime text,
contentVersion text,completedCount int,status int,result text,score text,grade text,lastUpdatedTime text,lastCompletedTime text,dateTime timestamp,PRIMARY KEY (id));

CREATE INDEX inx_cc_userId ON cassandraKeySpace.content_consumption (userId);
CREATE INDEX inx_cc_contentId ON cassandraKeySpace.content_consumption (contentId);
CREATE INDEX inx_cc_status ON cassandraKeySpace.content_consumption (status);
CREATE INDEX inx_cc_courseId ON cassandraKeySpace.content_consumption (courseId);


//creation of id = using timestamp and env , id and courseId both are same
//content id is from ekstep 
//status DRAFT("draft"), LIVE("live"), RETIRED("retired")
//contentType (pdf,video,word doc etc)
//tutor map<id,name>
CREATE TABLE IF NOT EXISTS cassandraKeySpace.course_management(id text, courseId text, courseName text,courseType text,contentId text,facultyId text,facultyName text,
organisationId text,organisationName text,enrollementStartDate text,enrollementEndDate text,courseDuration text,description text,status text,
addedBy text,addedByName text,publishedBy text,publishedByName text,createdDate text,publishedDate text,updatedDate text,updatedBy text,
updatedByName text,contentType text,noOfLectures text,createdfor list<text>,tutor map<text,text>,PRIMARY KEY (id));

CREATE INDEX inx_cm_facultyId ON cassandraKeySpace.course_management (facultyId);
CREATE INDEX inx_cm_organisationId ON cassandraKeySpace.course_management (organisationId);
CREATE INDEX inx_cm_courseId ON cassandraKeySpace.course_management (courseId);
CREATE INDEX inx_cm_course_name ON cassandraKeySpace.course_management (courseName);
CREATE INDEX inx_cm_status ON cassandraKeySpace.course_management (status);
CREATE INDEX inx_cm_contentId ON cassandraKeySpace.course_management (contentId);

//creation of id = one way hash of userName
//here id and userId both are same
// currently username and email is same
//email and username is unique
CREATE TABLE IF NOT EXISTS cassandraKeySpace.user(id text,userId text,userName text, email text,phone text,aadhaarNo text,createdDate text,updatedDate text,updatedBy text,
lastLoginTime text,status int,firstName text,lastName text,password text,avatar text,gender text,language text,state text,city text,zipcode text,PRIMARY KEY (id));

CREATE INDEX inx_u_email ON cassandraKeySpace.user (email);
CREATE INDEX inx_u_phone ON cassandraKeySpace.user (phone);
CREATE INDEX inx_u_status ON cassandraKeySpace.user (status);
CREATE INDEX inx_u_userId ON cassandraKeySpace.user (userId);
CREATE INDEX inx_u_userName ON cassandraKeySpace.user (userName);

//id is auth token
CREATE TABLE IF NOT EXISTS cassandraKeySpace.user_auth(id text, userId text,createdDate text,updatedDate text,source text,PRIMARY KEY (id));
CREATE INDEX inx_ua_userId ON cassandraKeySpace.user_auth (userId);
CREATE INDEX inx_ua_source ON cassandraKeySpace.user_auth (source);

CREATE TABLE IF NOT EXISTS cassandraKeySpace.organisation(id text, orgName text, description text,communityId text,createdBy text,createdByName text,createdDate text,
updatedDate text,updatedBy text,status int,relation text,parentOrgId text,orgType text,state text,city text,zipcode text,dateTime timestamp,PRIMARY KEY (id));

CREATE INDEX inx_org_orgName ON cassandraKeySpace.organisation (orgName);
CREATE INDEX inx_org_status ON cassandraKeySpace.organisation (status);
