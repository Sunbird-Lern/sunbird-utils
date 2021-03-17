ALTER TABLE sunbird.organisation ADD isTenant boolean;
ALTER TABLE sunbird.organisation ADD orgLocation text;
ALTER TABLE sunbird.organisation DROP parentorgid;
ALTER TABLE sunbird.organisation DROP orgtypeid;
DROP TABLE IF EXISTS sunbird.org_type;
DROP TABLE IF EXISTS sunbird.org_mapping;