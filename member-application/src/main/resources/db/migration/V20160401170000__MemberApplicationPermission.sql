INSERT INTO t_permission_definition (identifier, name, description)
 values ('member.edit', 'Member Edit', 'Edit member.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('coupon.edit', 'Coupon Edit', 'Edit coupon.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('member.mgmt', 'Member Management', 'Manage the member.', 'member.edit|coupon.edit');


insert into t_permission (role_id, identifier) values (1, 'member.edit');
insert into t_permission (role_id, identifier) values (1, 'coupon.edit');


INSERT INTO t_member_ext (user_id, level_id, name) VALUES (1, 1, 'admin');
