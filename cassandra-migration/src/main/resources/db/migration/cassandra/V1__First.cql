CREATE KEYSPACE IF NOT EXISTS sunbird WITH replication = {'class':'SimpleStrategy','replication_factor':1};
CREATE TABLE test (
  space text,
  key text,
  value text,
  PRIMARY KEY (space, key)
) with CLUSTERING ORDER BY (key ASC);