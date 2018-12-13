ALTER TABLE sunbird.user ADD (tncacceptedon timestamp, tncacceptedversion text);
ALTER TABLE sunbird.user DROP (tcstatus, tcupdateddate);
INSERT INTO sunbird.system_settings(id, field, value) VALUES('tncConfig', 'tncConfig', '{"latestVersion":"v1","v1":{"url":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html"}}');