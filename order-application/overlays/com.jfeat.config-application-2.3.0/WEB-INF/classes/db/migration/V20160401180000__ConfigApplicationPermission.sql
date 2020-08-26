INSERT INTO t_permission_definition (identifier, name, description)
 values ('config.view', 'Config View', 'View Config.');

INSERT INTO t_permission_definition (identifier, name, description)
 values ('config.edit', 'Config Edit', 'Edit Config.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('config.mgmt', 'Config Management', 'Manage the config.', 'config.edit');

--permission for admin role
insert into t_permission (role_id, identifier) values (1, 'config.view');
insert into t_permission (role_id, identifier) values (1, 'config.edit');

--menu
insert into t_menu (id, name, url, sort_order, parent_id) values (600, 'menu.config', 'config', 7, null);
