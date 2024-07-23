CREATE KEYSPACE IF NOT EXISTS sunbird_notifications WITH replication = {'class':'SimpleStrategy','replication_factor':3};

CREATE TABLE IF NOT EXISTS sunbird_notifications.notification_template(
templateId text,
type text,
ver text,
data text,
template_schema text,
createdOn timestamp,
lastUpdatedOn timestamp,
createdBy text,
lastUpdatedBy text,
PRIMARY KEY(templateId));

CREATE TABLE IF NOT EXISTS sunbird_notifications.notification_feed (
    id text PRIMARY KEY,
    category text,
    createdby text,
    createdon timestamp,
    action text,
    expireon timestamp,
    priority int,
    status text,
    updatedby text,
    updatedon timestamp,
    userid text
);

CREATE TABLE IF NOT EXISTS sunbird_notifications.action_template(
action text,
type text,
templateId text,
PRIMARY KEY(action));

CREATE INDEX IF NOT EXISTS idx_notification_userid ON sunbird_notifications.notification_feed (userid);