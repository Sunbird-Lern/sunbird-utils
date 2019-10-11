ALTER TABLE sunbird.user ADD prevUsedPhone text;
ALTER TABLE sunbird.user ADD prevUsedEmail text;
CREATE TABLE sunbird.user_cert (id text,certId text,userId text,store map<text,text>,otherLink text,accessCode text,createdAt timestamp, updatedAt timestamp, PRIMARY KEY (id));
CREATE INDEX inx_usrcert_user_id ON sunbird.user_cert(userId);
CREATE INDEX inx_usrcert_cert_id ON sunbird.user_cert(certId);

