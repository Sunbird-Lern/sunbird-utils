CREATE TABLE IF NOT EXISTS sunbird.bulk_upload_process_task(processId text,sequenceId int,iterationId int,data text,status int,successResult text,failureResult text,createdOn timestamp,lastUpdatedOn timestamp,PRIMARY KEY (processId ,sequenceId ));

ALTER TABLE  bulk_upload_process ADD  taskCount int;