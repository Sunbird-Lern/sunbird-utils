# sunbird-common

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4cd5e9f2008e4ee9a301ecda70703346)](https://app.codacy.com/app/sunbird-bot/sunbird-utils?utm_source=github.com&utm_medium=referral&utm_content=project-sunbird/sunbird-utils&utm_campaign=Badge_Grade_Settings)

This is the repository for common java utilities of Sunbird. Utilities include date/string/json utils, request validators and generic database access utilities for Cassandra and ElasticSearch databases.

The code in this repository is licensed under MIT License unless otherwise noted. Please see the [LICENSE](https://github.com/project-sunbird/sunbird-common/blob/master/LICENSE) file for details.

## This readme file contains the instruction to set up and run the sunbird-utils in local machine.
### Project setup:
Fork the below projects and clone it from git,
```shell
git clone https://github.com/Sunbird-Lern/sunbird-utils/<latest-branch>
```
Open a new Terminal In the path, 
#### (Project base path)/sunbird-utils
Run the below command, 
```shell
mvn clean install -DskipTests
``` 
Make sure the build is success and then,
open a new Terminal In the path,
#### (Project base path)/sunbird-utils/sunbird-cassandra-migration/cassandra-migration,
Run below command,
```shell
mvn clean install -DskipTests
``` 
### Command 1:
```shell
java -jar \
-Dcassandra.migration.scripts.locations=filesystem:<absolute or relative path>/db/migration/cassandra \
-Dcassandra.migration.cluster.contactpoints=localhost \
-Dcassandra.migration.cluster.port=9042 \
-Dcassandra.migration.cluster.username=username \
-Dcassandra.migration.cluster.password=password \
-Dcassandra.migration.keyspace.name=keyspace_name \
target/*-jar-with-dependencies.jar migrate
``` 
### Command 2:
```shell
java -cp "cassandra-migration-0.0.1-SNAPSHOT-jar-with-dependencies.jar" com.contrastsecurity.cassandra.migration.utils.MigrationScriptEntryPoint
```
The system environment listed below is required for command 2.
### System Env
```shell
sunbird_cassandra_keyspace=<keyspace_name>
sunbird_cassandra_migration_location="filesystem:<absolute or relative path>/db/migration/cassandra"
``` 