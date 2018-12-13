ALTER TABLE sunbird.user ADD (tncacceptedon timestamp, tncacceptedversion text);
ALTER TABLE sunbird.user DROP (tcstatus, tcupdateddate);
