insert into t_config_group (id, name) values (2, '微信配置');
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description, readonly) values
(2, 'token令牌', 'wx.token', 'String', '', 'sys', 1, '微信公众号后台的token. 进入 mp.weixin.qq.com 下面的开发-基本配置可以查看和修改。',0),
(2, '启用消息加密', 'wx.encrypt_message', 'boolean', 'false', 'sys', 1, '启用消息加解密',0),
(2, '消息加解密密钥', 'wx.encoding_aes_key', 'String', '', 'sys', 1, '消息加解密密钥',0),
(2, '应用ID', 'wx.app_id', 'String', '', 'sys', 1, '应用ID',0),
(2, '应用密钥', 'wx.app_secret', 'String', '', 'sys', 1, '应用密钥',0),
(2, '域名', 'wx.host', 'String', '', 'sys', 1, '微信应用部署的服务器域名',0),
(2, '商户ID', 'wx.partner_id', 'String', '', 'sys', 1, '微信支付商户号',0),
(2, '商户Key', 'wx.partner_key', 'String', '', 'sys', 1, '微信支付商户密钥',0),
(2, '证书路径', 'wx.cert_path', 'String', '', 'sys', 1, '证书路径,请前往 微信配置管理菜单 进行上传更新',1);
