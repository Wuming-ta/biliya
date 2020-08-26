
insert into t_menu (id, name, url, sort_order, parent_id) values (100, 'menu.identity', null, 2, null);

insert into t_menu (id, name, url, sort_order, parent_id) values (101, 'menu.user', 'user', 1, 100);
insert into t_menu (id, name, url, sort_order, parent_id) values (102, 'menu.role', 'role', 2, 100);
