ALTER TABLE sunbird.user ADD (managedBy text, accessCode text);
CREATE INDEX inx_u_managedBy ON sunbird.user(managedBy);