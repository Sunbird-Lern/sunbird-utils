CREATE TABLE IF NOT EXISTS sunbird.user_feed (id text,userId text,category text,priority int,status text,expireOn timestamp,data text,updatedOn timestamp,updatedby text,createdBy text,createdOn timestamp,PRIMARY KEY (id));
CREATE INDEX ON sunbird.user_feed (userId);
CREATE INDEX ON sunbird.user_feed (category);

// For fresh installation, if this step fails with an error message 'migration 1.90' failed,
// DROP TABLE IF EXISTS sunbird.user_feed;
// CREATE TABLE sunbird.user_feed(
// id text,
// category text,
// createdby text,
// createdon timestamp,
// data text,
// expireon timestamp,
// priority int,
// status int,
// updatedby text,
// updatedon timestamp,
// userid text,
// PRIMARY KEY(userId, category, id));
// UPDATE sunbird.cassandra_migration_version set success = True where version == '1.90';
// Rerun the migration job
