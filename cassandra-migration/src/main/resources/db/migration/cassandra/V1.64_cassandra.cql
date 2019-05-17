CREATE TRIGGER location_trigger ON sunbird.location USING 'org.sunbird.cassandra.Trigger';
CREATE TRIGGER organisation_trigger ON sunbird.organisation USING 'org.sunbird.cassandra.Trigger';
ALTER TABLE sunbird.user_courses ADD processingStatus text;
