ALTER TABLE sunbird_courses.course_batch ADD (created_date timestamp, start_date timestamp, end_date timestamp, enrollment_enddate timestamp, updated_date timestamp);
ALTER TABLE sunbird_courses.user_enrolments ADD (enrolled_date timestamp);
ALTER TABLE sunbird_courses.user_content_consumption ADD (last_access_time timestamp, last_completed_time timestamp, last_updated_time timestamp);
ALTER TABLE sunbird.page_management ADD (created_date timestamp, updated_date timestamp);
ALTER TABLE sunbird.page_section ADD (created_date timestamp, updated_date timestamp);