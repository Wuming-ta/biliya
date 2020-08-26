insert into t_config_group (id, name) values (40, '微信物流助手配置');

insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
 (40, '启用小程序物流助手', 'wx.express.enabled', 'boolean', 'false', '', 1, '启用小程序物流助手'),
 (40, '快递公司ID', 'wx.express.delivery_id', 'String', 'BEST', '', 1, '快递公司ID,如百世快递为BEST,请查看微信小程序文档'),
 (40, '快递公司名称', 'wx.express.delivery_name', 'String', '百世快递', '', 1, '快递公司ID,如百世快递为BEST,请查看微信小程序文档'),
 (40, '快递客户编码', 'wx.express.biz_id', 'String', '', '', 1, '快递客户编码,小程序后台绑定快递时使用的biz_id, 请查看微信小程序文档'),
 (40, '服务类型ID', 'wx.express.service_type', 'int', '1', '', 1, '服务类型ID, 查看微信小程序文档'),
 (40, '服务类型名称', 'wx.express.service_name', 'String', '普通快递', '', 1, '服务类型名称, 查看微信小程序文档'),
 (40, '默认快递发送公司名', 'wx.express.sender_company', 'String', '百世快递', '', 1, '默认快递发送公司名'),
 (40, '默认快递发送联系人', 'wx.express.sender_name', 'String', '小二', '', 1, '默认快递发送联系人'),
 (40, '默认快递发送手机', 'wx.express.sender_mobile', 'String', '13800000001', '', 1, '默认快递发送手机'),
 (40, '默认快递发送省', 'wx.express.sender_province', 'String', '广东省', '', 1, '默认快递发送省'),
 (40, '默认快递发送市', 'wx.express.sender_city', 'String', '广州市', '', 1, '默认快递发送市'),
 (40, '默认快递发送区', 'wx.express.sender_area', 'String', '天河区', '', 1, '默认快递发送区'),
 (40, '默认快递发送地址', 'wx.express.sender_address', 'String', '天河路', '', 1, '默认快递发送地址');
