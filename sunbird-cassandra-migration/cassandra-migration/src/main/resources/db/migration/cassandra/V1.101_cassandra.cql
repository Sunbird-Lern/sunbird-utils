CREATE TABLE IF NOT EXISTS sunbird.group (id text, name text, description text, activities map<text,text>, status text, membershipType text, createdBy text, createdOn timestamp, updatedBy text, updatedOn timestamp, PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS sunbird.user_group(userId text, groupId set<text>, PRIMARY KEY(userId));

CREATE TABLE IF NOT EXISTS sunbird.group_member(groupId text, role text, userId text, status text, createdBy text, createdOn timestamp , updatedBy text, updatedOn timestamp, removedBy text, removedOn timestamp, PRIMARY KEY(groupId, role, userId));
