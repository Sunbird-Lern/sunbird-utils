DROP INDEX sunbird_notifications.idx_notification_userid IF EXISTS;;

DROP TABLE sunbird_notifications.notification_feed IF EXISTS;;


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