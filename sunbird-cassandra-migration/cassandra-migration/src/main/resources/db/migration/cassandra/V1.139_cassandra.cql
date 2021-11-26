DELETE FROM sunbird.role WHERE id='TEACHER_BADGE_ISSUER' IF EXISTS;
DELETE FROM sunbird.role WHERE id='ANNOUNCEMENT_SENDER' IF EXISTS;
UPDATE sunbird.role set rolegroupid=['CONTENT_CURATION'] where id='CONTENT_REVIEWER';
UPDATE sunbird.role set rolegroupid=['CONTENT_CREATOR'] where id='CONTENT_CREATOR';
INSERT into sunbird.role_group (id,name) values ('CONTENT_CREATOR','Content Creator');