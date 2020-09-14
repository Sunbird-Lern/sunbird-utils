CREATE TABLE IF NOT EXISTS sunbird.user_feed_lookup(
 userid text,
 category text,
 feedid text,
 primary key((userid,category))
);