Drop index IF EXISTS sunbird.inx_usrcert_cert_id;
ALTER TABLE sunbird.user_cert DROP certId;
ALTER TABLE sunbird.organisation add keys map<text,frozen<list<text>>>;

