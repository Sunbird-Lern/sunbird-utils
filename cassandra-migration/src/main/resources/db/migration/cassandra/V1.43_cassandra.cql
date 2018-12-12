ALTER TABLE sunbird.user ADD (tacaccepteddate timestamp, tacversionid text);
ALTER TABLE sunbird.user DROP (tcstatus, tcupdateddate);
INSERT INTO sunbird.system_settings(id, field, value) VALUES('tacConfig', 'tacConfig', '{"version":"v1"}');