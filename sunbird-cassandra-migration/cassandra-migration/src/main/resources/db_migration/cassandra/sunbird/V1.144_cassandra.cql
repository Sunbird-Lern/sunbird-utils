ALTER TABLE sunbird.user ADD profiledetails text;
ALTER TABLE sunbird.organisation ADD organisationsubtype int;
ALTER TABLE sunbird.organisation ADD homeurl text;
ALTER TABLE sunbird.organisation ADD imgurl text;
INSERT INTO sunbird.system_settings (id, field, value) values ('orgTypeConfig', 'orgTypeConfig', '{"fields":[{"name":"School","value":2,"description":"School","displayName":"School","flagNameList":["isSchool"]},{"name":"Board","value":5,"description":"Board","displayName":"Board","flagNameList":["isBoard"]}]}');