ALTER TABLE  user_skills ADD  createdby text;
ALTER TABLE  user_skills ADD  createdon timestamp;
ALTER TABLE  user_skills ADD  lastupdatedby text;
ALTER TABLE  user_skills ADD  lastupdatedon timestamp;
ALTER TABLE  user_skills DROP  addedat;
ALTER TABLE  user_skills DROP  addedby

CREATE TABLE IF NOT EXISTS sunbird.email_template(name text,template text,createdOn timestamp,lastUpdatedOn timestamp,createdBy text,lastUpdatedBy text,PRIMARY KEY (name) );