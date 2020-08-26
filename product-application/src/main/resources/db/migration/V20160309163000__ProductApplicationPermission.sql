--Remove the comments if using identity module
INSERT INTO t_permission_definition (identifier, name, description)
 values ('product.category.edit', 'product.category.edit', 'product.category.edit');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('product.add', 'product.add', 'product.add');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('product.edit', 'product.edit', 'product.edit');
INSERT INTO t_permission_definition (identifier, name, description)
  values ('product.approve', 'product.approve', 'product.approve');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('product.direct_publish', 'product.direct_publish', 'product.direct_publish');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('product.delete', 'product.delete', 'product.delete');

INSERT INTO t_permission_definition (identifier, name, description)
 values ('fare_template.edit', 'fare_template.edit', 'fare_template.edit');

INSERT INTO t_permission_definition (identifier, name, description)
 values ('purchase_strategy.edit', 'purchase_strategy.edit', 'purchase_strategy.edit');

INSERT INTO t_permission_definition (identifier, name, description)
 values ('product_hit_word.edit', 'product_hit_word.edit', 'product_hit_word.edit');

INSERT INTO t_permission_definition (identifier, name, description)
 values ('product_brand.edit', 'product_brand.edit', 'product_brand.edit');

INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('product.mgmt', 'Product Management', 'Manage Products.', 'product.category.edit|product.add|product.edit|product.approve|product.delete|product.direct_publish|fare_template.edit|purchase_strategy.edit|product_hit_word.edit|product_brand.edit');

insert into t_permission (role_id, identifier) values (1, 'product.category.edit');
insert into t_permission (role_id, identifier) values (1, 'product.add');
insert into t_permission (role_id, identifier) values (1, 'product.edit');
insert into t_permission (role_id, identifier) values (1, 'product.approve');
insert into t_permission (role_id, identifier) values (1, 'product.delete');
insert into t_permission (role_id, identifier) values (1, 'product.direct_publish');
insert into t_permission (role_id, identifier) values (1, 'fare_template.edit');
insert into t_permission (role_id, identifier) values (1, 'purchase_strategy.edit');
insert into t_permission (role_id, identifier) values (1, 'product_hit_word.edit');
insert into t_permission (role_id, identifier) values (1, 'product_brand.edit');

