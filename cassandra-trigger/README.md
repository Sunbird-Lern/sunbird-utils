Run as a service

Compile cassandra-trigger module with all the dependency

1) mvn clean compile assembly:single
--- mvn clean install(if running as bin)

It will create a jar file(including all dependency) in target folder

Paste that jar in cassandra trigger set up directory i.e.(/etc/cassandra/triggers)

Need to restart cassandra for the trigger to get tied to the node.

create trigger trigger_name on sunbird.tablename using 'org.sunbird.cassandra.Trigger';

it will create a trigger on that table. Henceforth, every write operation on triggered table will generate an audit event which is recorded at
 -> default /var/log/cassandra/triggerAuditLog.log
custom filePath can be passed by adding the following line in /etc/cassandra/cassandra-env.sh at the bottom

JVM_OPTS="$JVM_OPTS -Dsunbird.cassandra.trigger.log=your-file-path"
Make sure file path has required file permission.
