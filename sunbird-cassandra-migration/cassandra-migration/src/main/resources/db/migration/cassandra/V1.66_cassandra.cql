CREATE KEYSPACE IF NOT EXISTS sunbird_courses WITH replication = {'class':'SimpleStrategy','replication_factor':1};

CREATE TABLE IF NOT EXISTS sunbird_courses.course_batch (courseid text, batchid text, createdby text, createddate text, createdfor list<text>, description text, enddate text, enrollmentenddate text, enrollmenttype text, mentors list<text>, name text, startdate text, status int, updateddate text, PRIMARY KEY (courseid, batchid));
CREATE INDEX inx_cou_bat_status ON sunbird_courses.course_batch (status);

CREATE TABLE IF NOT EXISTS sunbird_courses.user_courses (batchid text, userid text, active boolean, addedby text, completedon timestamp, completionpercentage int, contentstatus map<text, int>, courseid text, datetime timestamp, delta text, enrolleddate text, grade text, lastreadcontentid text, lastreadcontentstatus int, progress int, status int, PRIMARY KEY (batchid, userid));
CREATE INDEX inx_ucs_status ON sunbird_courses.user_courses (status);

CREATE TABLE IF NOT EXISTS sunbird_courses.content_consumption (userid text, contentid text, batchid text, courseid text, completedcount int, contentversion text, datetime timestamp, grade text, lastaccesstime text, lastcompletedtime text, lastupdatedtime text, progress int, result text, score text, status int, viewcount int, PRIMARY KEY (userid, contentid, batchid, courseid));
CREATE INDEX inx_cc_status ON sunbird_courses.content_consumption (status);

CREATE TABLE IF NOT EXISTS sunbird_courses.bulk_upload_process(id text, status int, data text, successResult text, failureResult text, uploadedBy text, uploadedDate text, processStartTime text, processEndTime text, ObjectType text, PRIMARY KEY (id));
CREATE INDEX inx_bup_status ON sunbird_courses.bulk_upload_process(status);
