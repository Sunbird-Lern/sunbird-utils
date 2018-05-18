CREATE TABLE IF NOT EXISTS sunbird.bulk_upload_process_task(processId text,sequenceId int,iterationId int,data text,status int,successResult text,failureResult text,createdOn timestamp,lastUpdatedOn timestamp,PRIMARY KEY (processId ,sequenceId ));

ALTER TABLE  bulk_upload_process ADD  taskCount int;

DROP TABLE user_external_identity;
CREATE TABLE IF NOT EXISTS sunbird.user_external_identity(id text,externalId text,provider text,userId text,createdOn timestamp,lastUpdatedOn timestamp,createdBy text,lastUpdatedBy text,PRIMARY KEY (id) );
ALTER TABLE sunbird.user DROP provider;
ALTER TABLE sunbird.user DROP regOrgId;
ALTER TABLE sunbird.user ADD channel text;

CREATE INDEX inx_uei_externalId ON sunbird.user_external_identity (externalId);
CREATE INDEX inx_uei_userId ON sunbird.user_external_identity (userId);
CREATE INDEX inx_uei_provider ON sunbird.user_external_identity (provider);