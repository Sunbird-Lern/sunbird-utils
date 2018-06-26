DROP TABLE sunbird.user_external_identity;
CREATE TABLE IF NOT EXISTS sunbird.user_external_identity(externalId text, provider text, idType text, userId text, createdOn timestamp, lastUpdatedOn timestamp, createdBy text, lastUpdatedBy text, PRIMARY KEY(provider, idType, externalId));
CREATE INDEX inx_uexit_user_id ON sunbird.user_external_identity(userId);