## Steps to run logstash in developer machine

1. Download logstash-6.7.0 and unzip it

2. Run kafka in localhost:9092 (default settings)

3. Create a kafka topic - local.lms.audit.events

4. Replace path of `/var/log/cassandra/triggerAuditLog.log` in `logstash.conf` file with the path of cassandra trigger log (if needed).

5. Replace `sincedb_path` to a valid one in developer machine

6. Navigate to logstash folder
```cd {logstash_home}```

7. Execute this command to run logstash
```bin/logstash -f {absolute path of logstash.conf} -w 1```
