CREATE TABLE IF NOT EXISTS sunbird.bulk_upload_process_tasks(processId text,sequenceId int,iterationId int,data text,status int,successResult text,failureResult text,createdTS timestamp,updatedTS timestamp,PRIMARY KEY (processId ,sequenceId ));

ALTER TABLE  bulk_upload_process ADD  taskCount int;