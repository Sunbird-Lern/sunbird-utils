CREATE TABLE IF NOT EXISTS sunbird.tac(id text, version text, text text, createdOn timestamp, lastUpdatedOn timestamp, createdBy text, lastUpdatedBy text, PRIMARY KEY(id));
ALTER TABLE sunbird.user ADD (tacstatus boolean, tacaccepteddate timestamp, tacversionid text);
ALTER TABLE sunbird.user DROP (tcstatus, tcupdateddate);
INSERT INTO sunbird.tac(id, version ,text) VALUES ('1','v1','<!doctype html><html></html>');
INSERT INTO sunbird.system_settings(id, field, value) VALUES('tacConfig', 'tacConfig', '{"default":"v1"}');