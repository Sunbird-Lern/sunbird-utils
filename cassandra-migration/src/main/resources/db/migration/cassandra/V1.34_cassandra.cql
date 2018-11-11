ALTER TABLE sunbird.user DROP frameworkdata;

ALTER TABLE sunbird.user ADD framework map<text, frozen<List<text>>>;
