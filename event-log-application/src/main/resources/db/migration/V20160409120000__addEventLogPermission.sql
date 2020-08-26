INSERT INTO t_permission_definition (identifier, name, description)
 values ('EventLogApplication.view', 'EventLogApplication View', 'View the EventLogApplication information.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('EventLogApplication.mgmt', 'EventLogApplication Management', 'Manage the EventLogApplication.', 'EventLogApplication.view');

 -- permission for admin
 insert into t_permission (role_id, identifier) values (1, 'EventLogApplication.view');


