# ExternalIdDecryption

# How to run
  - require env variable to be set : </br>
  
        - cassandra_host
        - sunbird_cassandra_keyspace
        - sunbird_cassandra_port
        - sunbird_encryption_key
   
   - mvn clean compile assembly:single
   - cd target
   - java -jar ExternalIdDecyptionScript-1.0-SNAPSHOT-jar-with-dependencies.jar