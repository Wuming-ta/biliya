INSERT INTO t_permission_definition (identifier, name, description)
 values ('merchant.manage', 'merchant Manage', 'Manage the merchant information');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('merchant.handle', 'merchant Handle', 'Handle the merchant information');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('merchant.mgmt', 'merchant Management', 'Manage the merchant', 'merchant.manage|merchant.handle');

insert into t_permission (role_id, identifier) values(1, 'merchant.manage');
