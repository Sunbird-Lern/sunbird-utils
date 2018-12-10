DROP TABLE IF EXISTS sunbird.config_path_audit;
CREATE TABLE IF NOT EXISTS sunbird.config_path_audit(id text, cloud_store_type text, cloud_store_account text, cloud_store_container text, cloud_store_path text, created_date bigint, version bigint, PRIMARY KEY ((id, cloud_store_type), created_date)) WITH CLUSTERING ORDER BY (created_date DESC);
CREATE INDEX inx_created_date ON sunbird.config_path_audit (created_date);

CREATE KEYSPACE IF NOT EXISTS qmzbm_form_service WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
CREATE TABLE IF NOT EXISTS qmzbm_form_service.form_data(root_org text, framework text, type text, subtype text, action text, component text, created_on timestamp, data text, last_modified_on timestamp, PRIMARY KEY((root_org, framework, type, subtype, action, component)));
