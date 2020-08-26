--Remove the comments if using identity module
INSERT INTO t_permission_definition (identifier, name, description)
 values ('order.view', 'order View', 'View the order information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('order.edit', 'order Edit', 'Edit the order information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('order.delete', 'order Delete', 'Delete order.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('order.mgmt', 'order Management', 'Manage the order.', 'order.view|order.edit|order.delete');

insert into t_permission (role_id, identifier) values (1, 'order.view');
insert into t_permission (role_id, identifier) values (1, 'order.edit');
insert into t_permission (role_id, identifier) values (1, 'order.delete');
