CREATE KEYSPACE IF NOT EXISTS qmzbm_form_service WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
CREATE TABLE IF NOT EXISTS qmzbm_form_service.form_data(root_org text, framework text, type text, subtype text, action text, component text, created_on timestamp, data text, last_modified_on timestamp, PRIMARY KEY((root_org, framework, type, subtype, action, component)));
