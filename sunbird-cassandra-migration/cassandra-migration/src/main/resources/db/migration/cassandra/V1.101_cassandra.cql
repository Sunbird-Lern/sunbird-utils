CREATE TABLE IF NOT EXISTS sunbird.group (id text, name text, description text, activities list<frozen <map<text,text>>>, status text, membershipType text, createdBy text, createdOn timestamp, updatedBy text, updatedOn timestamp, PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS sunbird.user_group(userId text, groupId set<text>, PRIMARY KEY(userId));

CREATE TABLE IF NOT EXISTS sunbird.group_member(groupId text, role text, userId text, status text, createdBy text, createdOn timestamp , updatedBy text, updatedOn timestamp, removedBy text, removedOn timestamp, PRIMARY KEY(groupId, userId));

ALTER TABLE sunbird.usr_external_identity ADD status text;

CREATE INDEX IF NOT EXISTS idx_group_status ON group (status);

CREATE INDEX IF NOT EXISTS idx_group_member_status ON group_member  (status);