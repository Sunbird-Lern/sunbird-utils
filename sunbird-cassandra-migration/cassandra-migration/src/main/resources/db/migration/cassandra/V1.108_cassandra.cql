CREATE KEYSPACE IF NOT EXISTS sunbird_groups WITH replication = {'class':'SimpleStrategy','replication_factor':3};

CREATE TABLE IF NOT EXISTS sunbird_groups.group (id text, name text, description text, activities list<frozen <map<text,text>>>, status text, membershipType text, createdBy text, createdOn timestamp, updatedBy text, updatedOn timestamp, PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS sunbird_groups.user_group(userId text, groupId set<text>, PRIMARY KEY(userId));

CREATE TABLE IF NOT EXISTS sunbird_groups.group_member(groupId text, role text, userId text, status text, createdBy text, createdOn timestamp , updatedBy text, updatedOn timestamp, removedBy text, removedOn timestamp, PRIMARY KEY(groupId, userId));

CREATE INDEX IF NOT EXISTS idx_group_status ON sunbird_groups.group (status);

CREATE INDEX IF NOT EXISTS idx_group_member_status ON sunbird_groups.group_member  (status);

CREATE TABLE IF NOT EXISTS sunbird.user_lookup(
 type text,
 value text,
 userId text,
 primary key((type,value))
);