ALTER TABLE  user_skills ADD  createdby text;
ALTER TABLE  user_skills ADD  createdon timestamp;
ALTER TABLE  user_skills ADD  lastupdatedby text;
ALTER TABLE  user_skills ADD  lastupdatedon timestamp;
ALTER TABLE  user_skills DROP  addedat;
ALTER TABLE  user_skills DROP  addedby;