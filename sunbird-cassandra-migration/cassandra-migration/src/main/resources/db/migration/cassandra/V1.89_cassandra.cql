CREATE TABLE sunbird.user_feed (id text,userId text,category text,priority int,status text,expireOn timestamp,data text,updatedOn timestamp,updatedby text,createdBy text,createdOn timestamp,PRIMARY KEY (id));
CREATE INDEX ON sunbird.user_feed (userId);
CREATE INDEX ON sunbird.user_feed (category);