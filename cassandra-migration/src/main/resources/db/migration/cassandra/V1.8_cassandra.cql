CREATE TABLE IF NOT EXISTS sunbird.location(id text, name text,code text,type text,parentid text,PRIMARY KEY (id));
CREATE INDEX inx_loc_code ON sunbird.location (code);
ALTER TABLE sunbird.organisation ADD locationIds list<text>;
ALTER TABLE sunbird.organisation ADD latitude float;
ALTER TABLE sunbird.organisation ADD longitude float;