ALTER TABLE sunbird.user_cert DROP certId;
ALTER TABLE sunbird.organisation add keys map<text,frozen<list<text>>>;

