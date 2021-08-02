CREATE TABLE sunbird.user_roles(
	userid text,
	role text,
	scope text,
	createdDate text,
	createdBy text,
	updatedDate text,
	updatedBy text,
 PRIMARY KEY (userid, role)
 );
