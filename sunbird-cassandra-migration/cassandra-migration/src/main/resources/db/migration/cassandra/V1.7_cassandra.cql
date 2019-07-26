update page_section set display='{"name":{"en":"Popular Template","hi":"नवीनतम पाठ्यक्रम"}}'  where id='01228383133972889627';
update page_section set display='{"name":{"en":"Popular Worksheet","hi":"लोकप्रिय वर्कशीट"}}'  where id='01228382966064742425';
update page_section set display='{"name":{"en":"Latest Courses","hi":"नवीनतम पाठ्यक्रम"}}'  where id='01228382278062080019';
update page_section set display='{"name":{"en":"Popular Worksheet","hi":"लोकप्रिय वर्कशीट"}}'  where id='01228383082462412826';
update page_section set display='{"name":{"en":"Popular Simulation","hi":"लोकप्रिय सिमुलेशन"}}'  where id='01228382897002905629';
update page_section set display='{"name":{"en":"Latest Courses","hi":"नवीनतम पाठ्यक्रम"}}'  where id='01228382681137152020';
update page_section set display='{"name":{"en":"Popular Story","hi":"लोकप्रिय कहानी"}}'  where id='01228382662997606424';
update page_section set display='{"name":{"en":"Popular Courses","hi":"लोकप्रिय पाठ्यक्रम"}}'  where id='01228382337862041618';
update page_section set display='{"name":{"en":"Popular Template","hi":"लोकप्रिय टेम्पलेट"}}'  where id='01228383171081011228';
update page_section set display='{"name":{"en":"Popular Courses","hi":"लोकप्रिय पाठ्यक्रम"}}'  where id='01228382243946496017';
update page_section set display='{"name":{"en":"Popular Story","hi":"लोकप्रिय कहानी"}}'  where id='01228383384379392023';
update page_section set display='{"name":{"en":"Popular Simulation","hi":"लोकप्रिय सिमुलेशन"}}'  where id='01228383260541747230';

CREATE TABLE IF NOT EXISTS sunbird.location(id text, name text,code text,type text,parentid text,PRIMARY KEY (id));
CREATE INDEX inx_loc_code ON sunbird.location (code);
ALTER TABLE sunbird.organisation ADD locationIds list<text>;