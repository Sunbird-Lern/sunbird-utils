update sunbird_notifications.notification_template set data='{"title": "${param1} has been deleted by ${param2}"}' where templateId='context-deleted';

update sunbird_notifications.notification_template  set data='{"title": "${param1} has been assigned to ${param2} by ${param3}' where templateId='context-assigned';
