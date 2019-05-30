ALTER TABLE sunbird.user DROP maskEmail;
ALTER TABLE sunbird.user DROP maskPhone;
ALTER TABLE sunbird.user ADD maskedEmail text;
ALTER TABLE sunbird.user ADD maskedPhone text;
