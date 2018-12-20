CREATE TABLE IF NOT EXISTS sunbird.rate_limit(key text, unit text, count int, PRIMARY KEY(key, unit));
