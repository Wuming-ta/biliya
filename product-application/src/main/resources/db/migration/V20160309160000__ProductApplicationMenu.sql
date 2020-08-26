
insert into t_menu (id, name, url, sort_order, parent_id) values (200, 'menu.product_mgmt', null, 3, null);

insert into t_menu (id, name, url, sort_order, parent_id) values (201, 'menu.product_category', 'product_category', 1, 200);
insert into t_menu (id, name, url, sort_order, parent_id) values (202, 'menu.product', 'product', 2, 200);
insert into t_menu (id, name, url, sort_order, parent_id) values (203, 'menu.fare_template', 'fare_template', 3, 200);
insert into t_menu (id, name, url, sort_order, parent_id) values (204, 'menu.purchase_strategy', 'purchase_strategy', 4, 200);
insert into t_menu (id, name, url, sort_order, parent_id) values (205, 'menu.product_hit_word', 'product_hit_word', 5, 200);
insert into t_menu (id, name, url, sort_order, parent_id) values (206, 'menu.product_brand', 'product_brand', 6, 200);