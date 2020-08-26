

---- sys api auth
--insert into t_config_group (id, name) values (100, '内部系统访问配置');
--insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
--(100, '用户名', 'sys.auth.username', 'String', 'sys', 'sys', 1, 'BASIC验证的用户名'),
--(100, '密码', 'sys.auth.password', 'String', 'sys', 'sys', 1, 'BASIC验证的密码'),
--(100, '允许访问IP列表', 'sys.auth.allowips', 'String', '', 'sys', 1, '允许访问本系统的内部系统IP列表. 以逗号分隔，如192.168.0.1,192.168.0.2. 如果为空则不进行IP匹配判断。');
--
