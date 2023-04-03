DROP KEYSPACE IF EXISTS lock_db;

CREATE KEYSPACE IF NOT EXISTS lock_db WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
CREATE TABLE IF NOT EXISTS lock_db.lock ("resourceId" text, "resourceType" text,  "createdBy" text, "createdOn" timestamp, "creatorInfo" text, "deviceId" text, "expiresAt" timestamp, "lockId" uuid, "resourceInfo" text, PRIMARY KEY ("resourceId", "resourceType"));

CREATE INDEX lock_lockId_idx ON lock_db.lock ("lockId");
