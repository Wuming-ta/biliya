
insert into t_config_group (id, name) values (5, '支付宝配置');
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description, readonly) values
(5, '应用ID', 'ali.app_id', 'String', '', '', 1, '应用ID',0),
(5, '应用密钥', 'ali.app_secret', 'text', '', '', 1, '应用密钥',0),
(5, '支付宝公钥', 'ali.alipay_public_key', 'text', '', '', 1, '支付宝公钥，不配置则使用默认值，一旦配置就使用该值覆盖',0);

