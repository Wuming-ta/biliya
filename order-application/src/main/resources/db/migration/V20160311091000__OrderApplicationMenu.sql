insert into t_menu (id, name, url, sort_order, parent_id) values (300, 'menu.order_mgmt', null, 4, null);

insert into t_menu (id, name, url, sort_order, parent_id) values (301, 'menu.order', 'order', 1, 300);
insert into t_menu (id, name, url, sort_order, parent_id) values (302, 'menu.express', 'express', 3, 300);
insert into t_menu (id, name, url, sort_order, parent_id) values (303, 'menu.return', 'return_refund_order', 2, 300);
insert into t_menu (id, name, url, sort_order, parent_id) values (304, 'menu.order_statistic', 'order_statistic', 3, 300);
