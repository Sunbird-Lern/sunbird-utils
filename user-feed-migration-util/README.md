# ExternalIdDecryption
# Prerequisite: 
      -  Login to cassandra
      -  Note down the count.
      -  Take backUp of cassandra db.

      
# How to run
  - require env variable to be set : </br>

        - sunbird_cassandra_host
        - sunbird_cassandra_learner_keyspace
        - sunbird_cassandra_port
        - sunbird_cassandra_notification_keyspace

   
   - mvn clean compile assembly:single
   - cd target
   - java -jar user-feed-migration-util-1.0-SNAPSHOT-jar-with-dependencies.jar 
     
# Steps To Verify:
   - Login to cassandra
   - Compare the count given post script.
   - Run the Query: SELECT COUNT(*) FROM sunbird.user_feed;  
   - count after running SELECT COUNT(*) FROM sunbird_notifications.notification_feed should be double than count of the user_feed table.
   - At the end of the run the program will generate the file named ```preProcessecRecords.txt``` which has feedId so  its easy to identify the records which are successfully processed...
