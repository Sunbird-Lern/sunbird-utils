# ExternalIdDecryption
# Prerequisite: 
      -  Login to cassandra
      -  SELECT COUNT(*) FROM sunbird.user;
      -  Note down the count.
      -  Take backUp of cassandra db.
      -  Run the query : select id,flagsvalue,updateddate from sunbird.user;
      -  verify the column flagsvalue, updateddate are updated.
# How to run
  - require env variable to be set : </br>
  
        - sunbird_cassandra_host
        - sunbird_cassandra_keyspace
        - sunbird_cassandra_port
        - sunbird_encryption_key
   
   - mvn clean compile assembly:single
   - cd target
   - java -jar statevalidator-util-1.0-SNAPSHOT-jar-with-dependencies.jar
     
# Steps To Verify:
   - Login to cassandra
   - select id,flagsvalue,updateddate from sunbird.user;
   - verify the column flagsvalue, updateddate should be updated. flagsvalue should be containing int value, updateddate should be recent date when jar starts executing
   -  Run the Query: SELECT COUNT(*) FROM sunbird.user;
   - count after running and before running should matches.
   - At the end of the run the program will generate the file named ```preProcessecRecords.txt``` which has userid,flagvalue in format ```userid:flagvalue:statevalidation``` so  its easy to identify the records which are successfully processed...
   - Please check pick randomly from this file and check in the DB.
   - check the count of the file entries with count of the records in the table.
