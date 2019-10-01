CREATE TABLE sunbirdplugin.group (
	id text,
	contexttype text,
	contextid text,
	pluginid text,
	rolemap blob,
	visibility text,
	access text,
	status int,
	createdby text,
	createddate text,
	updatedby text,
	updateddate text,
	PRIMARY KEY ((contexttype, contextid), pluginid)
);

CREATE TABLE sunbirdplugin.groupmember (
	id text,
	groupid text,
	userid text,
	roles list<text>,
	status int,
	joiningdate text,
	createddate text,
	updateddate text,
	PRIMARY KEY (groupid, userid)
);

CREATE TABLE sunbirdplugin.threadconfig (
	id text,
	threadid text,
	groupid text,
	config blob,
	createddate text,
	updateddate text,
	PRIMARY KEY (groupid, threadid)
);