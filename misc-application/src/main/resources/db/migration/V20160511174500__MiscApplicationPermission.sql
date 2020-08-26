--Remove the comments if using identity module
INSERT INTO t_permission_definition (identifier, name, description)
 values ('MiscApplication.view', 'MiscApplication View', 'View the MiscApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('MiscApplication.edit', 'MiscApplication Edit', 'Edit the MiscApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('MiscApplication.delete', 'MiscApplication Delete', 'Delete MiscApplication information.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('MiscApplication.mgmt', 'MiscApplication Management', 'Manage the MiscApplication.', 'MiscApplication.view|MiscApplication.edit|MiscApplication.delete');

-- permission for admin
insert into t_permission (role_id, identifier) values (1, 'MiscApplication.view');
insert into t_permission (role_id, identifier) values (1, 'MiscApplication.edit');
insert into t_permission (role_id, identifier) values (1, 'MiscApplication.delete');