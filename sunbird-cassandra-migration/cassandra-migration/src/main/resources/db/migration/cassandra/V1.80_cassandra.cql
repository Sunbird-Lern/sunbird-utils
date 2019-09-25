ALTER TABLE sunbird.bulk_upload_process ADD telemetrycontext map<text,text>;
ALTER TABLE sunbird.user_cert add (isDeleted boolean, oldId text);