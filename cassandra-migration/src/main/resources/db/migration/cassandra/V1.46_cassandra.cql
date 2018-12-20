INSERT INTO sunbird.system_settings(id, field, value) VALUES('userProfileConfig', 'userProfileConfig', '{"fields":["firstName","lastName","profileSummary","avatar","countryCode","dob","email","gender","grade","language","location","phone","subject","userName","webPages","jobProfile","address","education","skills","badgeAssertions"],"publicFields":["firstName","lastName","profileSummary"],"privateFields":["email","phone"],"csv":{"supportedColumns":{"NAME":"firstName","MOBILE PHONE":"phone","EMAIL":"email","SCHOOL ID":"orgId","USER_TYPE":"userType","ROLES":"roles","USER ID":"userId","SCHOOL EXTERNAL ID":"orgExternalId"},"outputColumns":{"userId":"USER ID","firstName":"NAME","phone":"MOBILE PHONE","email":"EMAIL","orgId":"SCHOOL ID","orgName":"SCHOOL NAME","userType":"USER_TYPE","orgExternalId":"SCHOOL EXTERNAL ID"},"outputColumnsOrder":["userId","firstName","phone","email","organisationId","orgName","userType","orgExternalId"],"mandatoryColumns":["firstName","userType","roles"]},"read":{"excludedFields":["avatar","jobProfile","address","education","webPages","skills"]},"framework":{"fields":["board","gradeLevel","medium","subject","id"],"mandatoryFields":["board","gradeLevel","medium","id"]}}');

CREATE TABLE IF NOT EXISTS sunbird.org_external_identity(provider text, externalId text, orgId text, PRIMARY KEY(provider, externalId));

DROP KEYSPACE IF EXISTS lock_db;

CREATE KEYSPACE IF NOT EXISTS lock_db WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
CREATE TABLE IF NOT EXISTS lock_db.lock ("resourceId" text, "resourceType" text,  "createdBy" text, "createdOn" timestamp, "creatorInfo" text, "deviceId" text, "expiresAt" timestamp, "lockId" uuid, "resourceInfo" text, PRIMARY KEY ("resourceId", "resourceType"));

CREATE INDEX lock_lockId_idx ON lock_db.lock ("lockId");
