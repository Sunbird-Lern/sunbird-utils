CREATE TABLE IF NOT EXISTS sunbird_courses.user_activity_agg (
    activity_type text,
    activity_id text,
    context_id text,
    user_id text,
    agg Map<text, int>,
    agg_last_updated Map<text, timestamp>,
    PRIMARY KEY ((activity_type, activity_id), user_id, context_id)
);