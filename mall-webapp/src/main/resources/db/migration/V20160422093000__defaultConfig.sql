
insert into t_config_group (id, name) values (3, '其他配置');
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
(3, '默认角色', 'misc.customer_role_id', 'int', '4', 'sys', 1, '用户注册时默认添加的角色号。'),
(3, 'API服务URL', 'misc.api_url', 'String', 'http://localhost:10080', 'sys', 1, 'API服务提供者的web service URL前缀。'),
(3, '快递100API查询公司编号', 'express.customer', 'String', '', 'sys', 1, '使用快递100API查询物流信息使用的公司编号,当使用企业版时需要这个字段，详见www.kuaidi100.com'),
(3, '快递100API查询Key', 'express.key', 'String', '', 'sys', 1, '使用快递100API查询物流信息使用的密钥，详见www.kuaidi100.com'),
(3, '关注我们文章地址', 'follow.us.url', 'String', '', 'sys', 1, '首页关注我们功能跳转到的公众号文章的地址');

-- sys api auth
insert into t_config_group (id, name) values (100, '内部系统访问配置');
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
(100, '用户名', 'sys.auth.username', 'String', 'sys', 'sys', 1, 'BASIC验证的用户名'),
(100, '密码', 'sys.auth.password', 'String', 'sys', 'sys', 1, 'BASIC验证的密码'),
(100, '允许访问IP列表', 'sys.auth.allowips', 'String', '', 'sys', 1, '允许访问本系统的内部系统IP列表. 以逗号分隔，如192.168.0.1,192.168.0.2. 如果为空则不进行IP匹配判断。');



--platform seller
insert into t_platform_seller (seller_id) values (1);
