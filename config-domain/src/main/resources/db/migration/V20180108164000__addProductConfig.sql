
insert into t_config_group (id, name) values (4, '商品配置');

--default setting
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
(4, '分区1名称', 'product.partner_level_zone_1', 'String', '零元区', '', 1, '分区1名称'),
(4, '分区2名称', 'product.partner_level_zone_2', 'String', '精品区', '', 1, '分区1名称'),
(4, '分区3名称', 'product.partner_level_zone_3', 'String', '特价区', '', 1, '分区1名称'),
(4, '显示分成设置开关', 'product.show_settlement_setting', 'boolean', 'true', '', 1, '显示分成设置'),
(4, '显示规格设置开关', 'product.show_specification_setting', 'boolean', 'true', '', 1, '显示规格设置开关');

