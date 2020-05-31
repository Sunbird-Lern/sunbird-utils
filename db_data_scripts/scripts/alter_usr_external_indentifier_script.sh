#!/bin/bash

echo Cassandra Altering table USR_EXTERNAL_IDENTIFIER started

bin/cqlsh 10.0.0.30

echo connected to host::: 10.0.0.30

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
userid) to 'usr_external_identity.csv' with header=true and numprocesses=8 and maxattempts=10;

echo Data copied to usr_external_identity.csv

echo number of rows in the usr_external_identity.csv file

wc -l usr_external_identity.csv

echo dropping table sunbird.usr_external_identity

DROP TABLE IF EXISTS sunbird.usr_external_identity;

echo creating table sunbird.usr_external_identity

CREATE TABLE sunbird.usr_external_identity(
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
PRIMARY KEY(userId, idType, provider));

echo creating index inx_usrextid_user_id

CREATE INDEX inx_usrextid_user_id ON sunbird.usr_external_identity(userId);

echo Coping data from usr_external_identity.csv to usr_external_identity table

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
userid) from 'usr_external_identity.csv' with header=true and ingestrate=20000 and numprocesses=8;

echo Cassandra Altering table USR_EXTERNAL_IDENTIFIER ended