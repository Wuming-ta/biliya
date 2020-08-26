
insert into t_config_group (id, name) values (30, '打印配置');

--default setting
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
(30, '订单打印LOGO', 'print.order.logo', 'String', '', '', 1, '订单打印LOGO'),
(30, '订单打印标题', 'print.order.title', 'String', '商城', '', 1, '订单打印标题');


