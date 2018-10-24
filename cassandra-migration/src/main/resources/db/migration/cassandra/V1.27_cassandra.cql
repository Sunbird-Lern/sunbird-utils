ALTER TABLE bulk_upload_process ADD createdby text;
ALTER TABLE bulk_upload_process ADD createdon timestamp;
ALTER TABLE bulk_upload_process ADD lastupdatedon timestamp;
CREATE INDEX inx_org_isrootorg ON sunbird.organisation (isrootorg);