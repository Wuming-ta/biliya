insert into t_menu (id, name, url, sort_order, parent_id, icon) values (1200, 'menu.merchant_mgmt', null, 11, null, '<i class="fa fa-car" aria-hidden="true"></i>');

insert into t_menu (id, name, url, sort_order, parent_id) values (1201, 'menu.merchant_config', 'merchant_config', 1, 1200);
insert into t_menu (id, name, url, sort_order, parent_id) values (1202, 'menu.settled_merchant', 'settled_merchant', 2, 1200);

insert into t_menu (id, name, url, sort_order, parent_id) values (1203, 'menu.settled_merchant_info', 'settled_merchant_info', 3, 1200);
insert into t_menu (id, name, url, sort_order, parent_id) values (1204, 'menu.settled_merchant_coupon', 'settled_merchant_coupon', 4, 1200);
insert into t_menu (id, name, url, sort_order, parent_id) values (1205, 'menu.settled_merchant_settlement', 'settled_merchant_settlement', 5, 1200);
