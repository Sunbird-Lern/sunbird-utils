ALTER TABLE sunbird.user ADD managedBy text;
ALTER TABLE sunbird.user ADD accessCode text;
CREATE INDEX inx_u_managedBy ON sunbird.user(managedBy);