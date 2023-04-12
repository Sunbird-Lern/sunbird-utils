# User And Organisation Data Sync To Elasticsearch 

**Command to run the jar:**

1. To sync user data

`java -jar SyncUserOrgDataToES.jar "csv file path" "learner_port" "learner_ip" "batch_count" "object_type" "dry_run"`

**Example:**

java -jar SyncUserOrgDataToES.jar "/home/user/userids.csv" "9000" "127.0.0.1" "100" "user" "false"


2. To sync org data

`java -jar SyncUserOrgDataToES.jar "csv file path" "learner_port" "learner_ip" "batch_count" "object_type" "dry_run"`

**Example:**

java -jar SyncUserOrgDataToES.jar "/home/org/orgids.csv" "9000" "127.0.0.1" "100" "organisation" "false"

**Note:** 
1. dry_run as true will not make learner API call.
   dry_run as false will make learner API call.
   
2. batch_count is the count of ids it will read from csv file to sync user/org in one API call.

