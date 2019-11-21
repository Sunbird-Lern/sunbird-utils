CREATE TABLE IF NOT EXISTS sunbird_courses.certificate_templates(courseid text,batchid text,name text,template text,filters text,addedby text,lastupdatedon timestamp,PRIMARY KEY (courseid,name) );
ALTER TABLE sunbird_courses.course_batch ADD cert_templates map<text,frozen<map<text,text>>>;
