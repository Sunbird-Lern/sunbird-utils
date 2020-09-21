CREATE TABLE IF NOT EXISTS sunbird.user_consent (
    id text,
    user_id text,
    consumer_id text,
    consumer_type text,
    object_id text,
    object_type text,
    status text,
    expiry timestamp,
    categories list<text>,
    consent_data text,
    created_on timestamp,
    last_updated_on timestamp,
    PRIMARY KEY (user_id, consumer_id, object_id)
);