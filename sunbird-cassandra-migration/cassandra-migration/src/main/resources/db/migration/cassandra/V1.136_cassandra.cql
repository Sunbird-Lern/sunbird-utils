DROP INDEX IF EXISTS sunbird_notifications.idx_notification_userid;

DROP TABLE IF EXISTS sunbird_notifications.notification_feed;


CREATE TABLE sunbird_notifications.notification_feed (
    userid text,
    id text,
    action text,
    category text,
    createdby text,
    createdon timestamp,
    expireon timestamp,
    priority int,
    status text,
    updatedby text,
    updatedon timestamp,
    version text,
    PRIMARY KEY (userid, id)
);