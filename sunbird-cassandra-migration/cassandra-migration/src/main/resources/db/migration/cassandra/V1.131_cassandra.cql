update sunbird_notifications.notification_template  set data='{"title": "${param1} has been assigned to ${param2} by ${param3}"}' where templateId='context-assigned';

insert into sunbird_notifications.notification_template(templateId, type, ver, data, template_schema) values ('certificate-userfeed','JSON','4.3.0','{"title": "${param1}", "description": "${param2}"}', '{"$schema":"#/definition/params","title":"${param1}","description":"${param2}","type":"object","properties":{"param1":{"description":"courseName","type":"string"},"param2":{"description":"user feed message","type":"string"}},"required":["param1","param2"] }');

insert into sunbird_notifications.action_template(action, templateId, type) values('certificateUpdate','certificate-userfeed', 'feed');