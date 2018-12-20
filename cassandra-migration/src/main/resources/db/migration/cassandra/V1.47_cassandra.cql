CREATE TABLE IF NOT EXISTS sunbird.rate_limit(key text, unit text, count int, rate int, PRIMARY KEY(key, unit));
