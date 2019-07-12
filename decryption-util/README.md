# ExternalIdDecryption
# PreRequesties: 
      -  Login to cassandra
      -  select externalid,originalexternalid from sunbird.usr_external_identity;
      -  verify the column externalid, originalexternalid should be in encrpted format
# How to run
  - require env variable to be set : </br>
  
        - cassandra_host
        - sunbird_cassandra_keyspace
        - sunbird_cassandra_port
        - sunbird_encryption_key
   
   - mvn clean compile assembly:single
   - cd target
   - java -jar decryptionUtil-1.0-SNAPSHOT-jar-with-dependencies.jar 
     
# PostRequesties:
   - Login to cassandra
   - select externalid,originalexternalid from sunbird.usr_external_identity;
   - verify the column externalid, originalexternalid should be in decrypted format.
