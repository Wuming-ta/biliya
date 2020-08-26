--Remove the comments if using identity module
INSERT INTO t_permission_definition (identifier, name, description)
 values ('identity.view', 'IdentityApplication View', 'View the IdentityApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('identity.edit', 'IdentityApplication Edit', 'Edit the IdentityApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('identity.delete', 'IdentityApplication Delete', 'Delete IdentityApplication.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('identity.mgmt', 'IdentityApplication Management', 'Manage the IdentityApplication.', 'identity.view|identity.edit|identity.delete');



--default admin role
insert into t_role (id, system, name, description) values (1, 1, 'admin', '管理员');

insert into t_permission (role_id, identifier) values (1, 'identity.view');
insert into t_permission (role_id, identifier) values (1, 'identity.edit');
insert into t_permission (role_id, identifier) values (1, 'identity.delete');

-- create super admin
insert into t_user (id, email, name, login_name, phone, password, status, salt, invitation_code, uid)
 values (1, '', 'Administrator', 'admin', 'admin', '6609484d39036b4eaf76c22769677e3260b6b34f', 'NORMAL', '1c7213c60d7f4463', 'a1b2c3', 'U00000001');
insert into t_user_role (user_id, role_id) values (1, 1);
