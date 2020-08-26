--Remove the comments if using identity module
INSERT INTO t_permission_definition (identifier, name, description)
 values ('CooperativePartnerApplication.view', 'CooperativePartnerApplication View', 'View the CooperativePartnerApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('CooperativePartnerApplication.edit', 'CooperativePartnerApplication Edit', 'Edit the CooperativePartnerApplication information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('CooperativePartnerApplication.delete', 'CooperativePartnerApplication Delete', 'Delete CooperativePartnerApplication.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('CooperativePartnerApplication.mgmt', 'CooperativePartnerApplication Management', 'Manage the CooperativePartnerApplication.', 'CooperativePartnerApplication.view|CooperativePartnerApplication.edit|CooperativePartnerApplication.delete');

-- permission for admin
insert into t_permission (role_id, identifier) values (1, 'CooperativePartnerApplication.view');
insert into t_permission (role_id, identifier) values (1, 'CooperativePartnerApplication.edit');
insert into t_permission (role_id, identifier) values (1, 'CooperativePartnerApplication.delete');


-- seller for admin
insert into t_seller (id, user_id, parent_id, partner_ship, partner_id, seller_ship, level) values (1, 1, null, 0, null, 1, 1);


--Remove the comments if using identity module
INSERT INTO t_permission_definition (identifier, name, description)
 values ('physical.seller.view', 'physical.seller View', 'View the physical.seller information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('physical.seller.edit', 'physical.seller Edit', 'Edit the physical.seller information.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('physical.seller.delete', 'physical.seller Delete', 'Delete physical.seller.');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('physical.seller.mgmt', 'physical.seller Management', 'Manage the physical.seller.', 'physical.seller.view|physical.seller.edit|physical.seller.delete');

-- permission for admin
insert into t_permission (role_id, identifier) values (1, 'physical.seller.view');
insert into t_permission (role_id, identifier) values (1, 'physical.seller.edit');
insert into t_permission (role_id, identifier) values (1, 'physical.seller.delete');
