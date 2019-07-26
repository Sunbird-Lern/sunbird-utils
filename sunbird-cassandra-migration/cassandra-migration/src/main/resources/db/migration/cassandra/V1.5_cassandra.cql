DELETE FROM sunbird.role WHERE id='COURSE_CREATOR' IF EXISTS;
DELETE FROM sunbird.role WHERE id='COURSE_ADMIN' IF EXISTS;
DELETE FROM sunbird.role WHERE id='ADMIN' IF EXISTS;
DELETE FROM sunbird.role_group WHERE id='ORG_MODERATOR' IF EXISTS;

DELETE FROM sunbird.role_group WHERE id='COURSE_CREATOR' IF EXISTS;
DELETE FROM sunbird.role_group WHERE id='COURSE_ADMIN' IF EXISTS;
DELETE FROM sunbird.role_group WHERE id='ADMIN' IF EXISTS;

insert into sunbird.role (id,name,rolegroupid,status) values ('OFFICIAL_TEXTBOOK_BADGE_ISSUER','Official TextBook Badge Issuer',['OFFICIAL_TEXTBOOK_BADGE_ISSUER'],1);
insert into sunbird.role (id,name,rolegroupid,status) values ('TEACHER_BADGE_ISSUER','Teacher Badge Issuer',['TEACHER_BADGE_ISSUER'],1);
insert into sunbird.role_group (id,name) values ('OFFICIAL_TEXTBOOK_BADGE_ISSUER','Official TextBook Badge Issuer');
insert into sunbird.role_group (id,name) values ('TEACHER_BADGE_ISSUER','Teacher Badge Issuer');

ALTER TABLE sunbird.report_tracking ADD resourceName text;