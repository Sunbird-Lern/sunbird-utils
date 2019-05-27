The implementation team will need to run the following two java programs.
Generate Cassandra script
    
java -jar MigrationScriptGen-1.0-SNAPSHOT-jar-with-dependencies.jar {csvFileInput} {cqlFileOutput} {channelName} {rootOrgId}  {Bearer apiKey}  {userToken} {baseUrl}


By running above jar it will generate two file 
 1: Log File which will show the error or the records which are not processed
 2: UserExtId.csv file which will have four columns a:userName b: userId c: treasuryId d: channel



2: The second script will take input above generated csv file and update the user. It can be run as a jar with 5 input path params
 
java -jar ExternalIdMigrationScript-1.0-SNAPSHOT-jar-with-dependencies.jar {CSVFileInput} {baseUrl} {realm} {clientId} {Bearer-apiKey}


 And will generate 2 output files
 1: Log file with errors
 2: FailedRecords.txt which will inform about the records which failed while  executing script.
