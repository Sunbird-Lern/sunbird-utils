DROP TABLE IF EXISTS sunbird.badge_class_extension;
CREATE TABLE IF NOT EXISTS sunbird.badge_class_extension(id text, issuerId text, rootOrgId text, type text, subtype text, roles list<text>, PRIMARY KEY (id));
