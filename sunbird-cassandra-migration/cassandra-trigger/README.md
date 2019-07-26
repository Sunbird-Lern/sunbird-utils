Steps to enable Cassandra Triggers:

1. Compile cassandra-trigger module with all dependency.

```
cd cassandra-trigger
mvn clean compile assembly:single
```

2. Copy the compiled JAR (i.e. cassandra-trigger-1.0.jar) to cassandra triggers (e.g. /etc/cassandra/triggers) folder.

3. Restart Cassandra.

```
sudo service cassandra stop
sudo service cassandra start
```

4. Trigger can be applied on a table using below command.

```
CREATE TRIGGER <trigger_name> ON <table_name> USING 'org.sunbird.cassandra.Trigger';
```

e.g.
```
CREATE TRIGGER location_trigger ON sunbird.location USING 'org.sunbird.cassandra.Trigger';
```

5. After trigger is created on a table, any modification of data in table will result in a corresponding audit event to be written into the audit log file. The default location of audit log file is "/var/log/cassandra/triggerAuditLog.log". 

6. To customise the log file path, edit JVM_OPTS in /etc/cassandra/cassandra-env.sh as mentioned below and restart Cassandra.

```
JVM_OPTS="$JVM_OPTS -Dsunbird_cassandra_audit_file_path=<your-audit-file-path>"
```
