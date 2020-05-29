#!/bin/bash

bin/cqlsh 10.0.0.30

copy sunbird.usr_external_identity(provider,
idtype,
externalid,
createdby,
createdon,
lastupdatedby,
lastupdatedon,
originalexternalid,
originalidtype,
originalprovider,
userid) to usr_external_identity.csv’ with header=true;

DROP INDEX IF EXISTS sunbird.inx_usrextid_user_id;

DROP TABLE IF EXISTS sunbird.usr_external_identity;

CREATE TABLE EXISTS sunbird.usr_external_identity(
externalId text,
provider text,
idType text,
userId text,
createdOn timestamp,
lastUpdatedOn timestamp,
createdBy text,
lastUpdatedBy text,
originalExternalId text,
originalIdType text,
originalProvider text,
PRIMARY KEY((userId, idType), provider));

CREATE INDEX inx_usrextid_user_id ON sunbird.usr_external_identity(userId);

copy sunbird.usr_external_identity(provider,
idtype,
externalid,
createdby,
createdon,
lastupdatedby,
lastupdatedon,
originalexternalid,
originalidtype,
originalprovider,
userid) from usr_external_identity.csv’ with header=true;