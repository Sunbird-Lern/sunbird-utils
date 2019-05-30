DELETE FROM sunbird.role WHERE id='ORG_MODERATOR' IF EXISTS;
insert into sunbird.system_settings (id,field,value) values ('phoneUnique','phoneUnique','true');
insert into sunbird.system_settings (id,field,value) values ('emailUnique','emailUnique','true');