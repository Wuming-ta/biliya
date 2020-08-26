INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.trial.view', 'Marketing Trial View', 'View marketing trial.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.trial.edit', 'Marketing Trial Edit', 'Edit marketing trial.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.trial.delete', 'Marketing Trial Delete', 'Delete marketing trial.');

 INSERT INTO t_permission_definition (identifier, name, description)
  values ('marketing.trial_application.view', 'Marketing Trial Application View', 'View marketing trial application.');
 INSERT INTO t_permission_definition (identifier, name, description)
  values ('marketing.trial_application.edit', 'Marketing Trial Application Edit', 'Edit marketing trial application.');
 INSERT INTO t_permission_definition (identifier, name, description)
  values ('marketing.trial_application.delete', 'Marketing Trial Application Delete', 'Delete marketing trial application.');


INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('marketing.trial.mgmt', 'Marketing Trial Management', 'Manage the marketing trial.', 'marketing.trial.view|marketing.trial.edit|marketing.trial.delete|marketing.trial_application.view|marketing.trial_application.edit|marketing.trial_application.delete');

insert into t_permission (role_id, identifier) values (1, 'marketing.trial.view');
insert into t_permission (role_id, identifier) values (1, 'marketing.trial.edit');
insert into t_permission (role_id, identifier) values (1, 'marketing.trial.delete');

insert into t_permission (role_id, identifier) values (1, 'marketing.trial_application.view');
insert into t_permission (role_id, identifier) values (1, 'marketing.trial_application.edit');
insert into t_permission (role_id, identifier) values (1, 'marketing.trial_application.delete');