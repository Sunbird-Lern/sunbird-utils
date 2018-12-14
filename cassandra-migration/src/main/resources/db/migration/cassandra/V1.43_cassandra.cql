ALTER TABLE sunbird.user ADD (tncacceptedon timestamp, tncacceptedversion text);
ALTER TABLE sunbird.user DROP (tcstatus, tcupdateddate);
CREATE TABLE IF NOT EXISTS sunbird.otp(type TEXT, key TEXT, otp TEXT, createdon timestamp,PRIMARY KEY (type, key));