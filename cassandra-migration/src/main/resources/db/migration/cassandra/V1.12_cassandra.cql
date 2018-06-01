DROP TABLE sunbird.user_external_identity;
CREATE TABLE IF NOT EXISTS sunbird.user_external_identity(externalId text,provider text,idType text,userId text,slug text,createdOn timestamp,lastUpdatedOn timestamp,createdBy text,lastUpdatedBy text,PRIMARY KEY (provider,idType,userId) );
CREATE INDEX inx_uxit_userId ON sunbird.user_external_identity (userId);
CREATE INDEX inx_uxit_slug ON sunbird.user_external_identity (slug);