insert into t_config (group_id, name, key_name, value_type, value, type, visible, description, readonly) values
(2, '小程序AppId', 'wx.wxa_appid', 'String', '', 'sys', 1, '微信小程序APP ID',0),
(2, '小程序密钥', 'wx.wxa_app_secret', 'String', '', 'sys', 1, '微信小程序APP密钥',0),
(2, '登录自动注册', 'wx.auto_reg', 'boolean', 'true', 'sys', 1, '微信登录后自动注册，如果设为false，则需要用户填手机号进行注册',0);
