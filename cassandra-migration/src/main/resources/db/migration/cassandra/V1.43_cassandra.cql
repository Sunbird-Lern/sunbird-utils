ALTER TABLE sunbird.user ADD (tncacceptedon timestamp, tncacceptedversion text);
ALTER TABLE sunbird.user DROP (tcstatus, tcupdateddate);
INSERT INTO sunbird.system_settings(id, field, value) VALUES('tncConfig', 'tncConfig', '{"latestVersion":"v1","v1":{"url":""}}');