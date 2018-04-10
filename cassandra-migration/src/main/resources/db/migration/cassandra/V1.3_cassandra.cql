CREATE TABLE IF NOT EXISTS sunbird.badge_class_extension(id text, issuerId text, rootOrgId text, type text, subtype text, roles list<text>, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS sunbird.user_badge_assertion(id text, createdTS timestamp, issuerId text ,badgeId text ,badgeClassName text , badgeClassImage text , assertionId text ,userId text ,PRIMARY KEY (id));
insert into sunbird.role (id,name,rolegroupid,status) values ('BOOK_CREATOR','Book Creator',['BOOK_CREATOR'],1);
insert into sunbird.role (id,name,rolegroupid,status) values ('BOOK_REVIEWER','Book Reviewer',['BOOK_REVIEWER'],1);
insert into sunbird.role_group (id,name) values ('BOOK_CREATOR','Book Creator');
insert into sunbird.role_group (id,name) values ('BOOK_REVIEWER','Book Reviewer');
