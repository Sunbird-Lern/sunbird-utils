Alter table sunbird_notifications.notification_feed add version text;

Alter table sunbird_notifications.notification_template add config map<text,text>;

insert into sunbird_notifications.notification_template(templateId, type, ver, data, template_schema) values ('user-added-to','JSON','4.3.0','{"title": "you have been added to ${param1} by ${param2}"}', '{"$schema":"#/definition/params","title":"params context","description":"properties Data","type":"object","properties":{"param1":{"description":"property 1 value","type":"string"},"param2":{"description":"property 2 value","type":"string"}},"required":["param1","param2"]}');

insert into sunbird_notifications.notification_template(templateId, type, ver, data, template_schema) values ('user-exit','JSON','4.3.0','{"title": "${param1} has left the ${param2}"}', '{"$schema":"#/definition/params","title":"params context","description":"properties Data","type":"object","properties":{"param1":{"description":"property 1 value","type":"string"},"param2":{"description":"property 2 value","type":"string"}},"required":["param1","param2"]}');

insert into sunbird_notifications.notification_template(templateId, type, ver, data, template_schema) values ('user-removed','JSON','4.3.0','{"title": "you have been removed from the ${param1} by ${param2}"}', '{"$schema":"#/definition/params","title":"params context","description":"properties Data","type":"object","properties":{"param1":{"description":"property 1 value","type":"string"},"param2":{"description":"property 2 value","type":"string"}},"required":["param1","param2"]}');

insert into sunbird_notifications.notification_template(templateId, type, ver, data, template_schema) values ('context-deleted','JSON','4.3.0','{"title": "{param1} has been deleted by ${param2}"}', '{"$schema":"#/definition/params","title":"params context","description":"properties Data","type":"object","properties":{"param1":{"description":"property 1 value","type":"string"},"param2":{"description":"property 2 value","type":"string"}},"required":["param1","param2"]}');

insert into sunbird_notifications.notification_template(templateId, type, ver, data, template_schema) values ('context-assigned','JSON','4.3.0','{"title": "${param1} has been assigned to ${param2} by ${param3}"}', '{"$schema":"#/definition/params","title":"params context","description":"properties Data","type":"object","properties":{"param1":{"description":"property 1 value","type":"string"},"param2":{"description":"property 2 value","type":"string"},"param3":{"description":"property 3 value","type":"string"}},"required":["param1","param2","param3   ] }');

insert into sunbird_notifications.notification_template(templateId, type, ver, data, template_schema) values ('context-unassigned','JSON','4.3.0','{"title": "${param1} has been unassigned to ${param2} by ${param3}"}', '{"$schema":"#/definition/params","title":"params context","description":"properties Data","type":"object","properties":{"param1":{"description":"property 1 value","type":"string"},"param2":{"description":"property 2 value","type":"string"},"param3":{"description":"property 3 value","type":"string"}},"required":["param1","param2","param3   ] }');

insert into sunbird_notifictions.action_template(action, templateId, type) values('member-added','user-added-to', 'feed');

insert into sunbird_notifictions.action_template(action, templateId, type) values('member-removed','user-removed', 'feed');

insert into sunbird_notifictions.action_template(action, templateId, type) values('member-exit','user-exit', 'feed');

insert into sunbird_notifictions.action_template(action, templateId, type) values('group-deleted','context-deleted', 'feed');

insert into sunbird_notifictions.action_template(action, templateId, type) values('group-activity-added','context-assigned', 'feed');

insert into sunbird_notifictions.action_template(action, templateId, type) values('group-activity-removed','context-unassigned', 'feed');
