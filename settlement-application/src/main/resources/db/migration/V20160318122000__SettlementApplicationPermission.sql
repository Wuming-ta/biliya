--Remove the comments if using identity module
INSERT INTO t_permission_definition (identifier, name, description)
 values ('SettlementApplication.view', 'SettlementApplication View', 'View the SettlementApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('SettlementApplication.edit', 'SettlementApplication Edit', 'Edit the SettlementApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('SettlementApplication.delete', 'SettlementApplication Delete', 'Delete SettlementApplication.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('SettlementApplication.mgmt', 'SettlementApplication Management', 'Manage the SettlementApplication.', 'SettlementApplication.view|SettlementApplication.edit|SettlementApplication.delete');

-- permission for admin
insert into t_permission (role_id, identifier) values (1, 'SettlementApplication.view');
insert into t_permission (role_id, identifier) values (1, 'SettlementApplication.edit');
insert into t_permission (role_id, identifier) values (1, 'SettlementApplication.delete');

