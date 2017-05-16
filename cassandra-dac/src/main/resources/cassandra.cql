CREATE KEYSPACE IF NOT EXISTS cassandraKeySpace WITH replication = {'class':'SimpleStrategy','replication_factor':3};
to change cluster name
UPDATE system.local SET cluster_name = 'sunbird' where key='local';
ALTER USER cassandra WITH PASSWORD 'password';
USE cassandraKeySpace


CREATE TABLE IF NOT EXISTS cassandraKeySpace.content_consumption(id text, contentId text, userId text,viewPosition text,viewCount text,lastAccessTime timestamp,completedCount text,status int,lastUpdatedTime timestamp,lastCompletedTime timestamp,PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS cassandraKeySpace.course_enrollment(id text, courseId text, courseName text,userId text,enrolledDate timestamp,description text,tocUrl text,status text,active boolean,delta text,PRIMARY KEY (id));

CREATE INDEX inx_ce_userId ON cassandraKeySpace.course_enrollment (userId);

CREATE INDEX inx_ce_courseId ON cassandraKeySpace.course_enrollment (courseId);

CREATE INDEX inx_ce_course_name ON cassandraKeySpace.course_enrollment (courseName);

CREATE INDEX inx_cc_userId ON cassandraKeySpace.content_consumption (userId);

CREATE INDEX inx_cc_contentId ON cassandraKeySpace.content_consumption (contentId);

CREATE INDEX inx_cc_status ON cassandraKeySpace.content_consumption (status);

CREATE INDEX inx_ce_status ON cassandraKeySpace.course_enrollment (status);
