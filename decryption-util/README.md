# ExternalIdDecryption
# Prerequisite: 
      -  Login to cassandra
      -  SELECT COUNT(*) FROM sunbird.usr_external_identity;  
      -  Note down the count.
      -  Take backUp of cassandra db.
      -  Run the query : select externalid,originalexternalid from sunbird.usr_external_identity;
      -  verify the column externalid, originalexternalid should be in encrypted format
# How to run
  - require env variable to be set : </br>

        - sunbird_cassandra_host
        - sunbird_cassandra_keyspace
        - sunbird_cassandra_port
        - sunbird_encryption_key
   
   - mvn clean compile assembly:single
   - cd target
   - java -jar decryptionUtil-1.0-SNAPSHOT-jar-with-dependencies.jar 
     
# Steps To Verify:
   - Login to cassandra
   - select externalid,originalexternalid from sunbird.usr_external_identity;
   - verify the column externalid, originalexternalid should be in decrypted format.
   -  Run the Query: SELECT COUNT(*) FROM sunbird.usr_external_identity;  
   - count after running and before running should matches.
   - At the end of the run the program will generate the file named ```preProcessecRecords.txt``` which has provider,idType,externalId in format ```provider:idtype:externalid``` so  its easy to identify the records which are successfully processed...
