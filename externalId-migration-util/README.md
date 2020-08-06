# ExternalIdDecryption
# Prerequisite: 
      -  Login to cassandra
      -  Note down the count.
      -  Take backUp of cassandra db.

      
# How to run
  - require env variable to be set : </br>

        - sunbird_cassandra_host
        - sunbird_cassandra_keyspace
        - sunbird_cassandra_port

   
   - mvn clean compile assembly:single
   - cd target
   - java -jar externalId-migration-util-1.0-SNAPSHOT-jar-with-dependencies.jar 
     
# Steps To Verify:
   - Login to cassandra
   - Compare the count given post script.
   - Run the Query: SELECT COUNT(*) FROM sunbird.usr_external_identity;  
   - count after running and before running should matches.
   - At the end of the run the program will generate the file named ```preProcessecRecords.txt``` which has userId,provider in format ```userId,provider``` so  its easy to identify the records which are successfully processed...
