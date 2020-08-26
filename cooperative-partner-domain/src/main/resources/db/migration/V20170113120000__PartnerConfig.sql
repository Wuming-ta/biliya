
insert into t_config_group (id, name) values (400, '合作伙伴配置');

--default setting
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description, readonly)
 values (400, '线下经销商申请成为皇冠商开关', 'partner.can_apply_crown', 'boolean', 'false', 'sys', 1, '线下经销商申请成为皇冠商开关', 0),
 (400, '线下经销商扫码申请成为皇冠商开关', 'partner.can_code_apply_crown', 'boolean', 'false', 'sys', 1, '线下经销商扫码申请成为皇冠商开关', 0),
 (400, '线下经销商申请成为皇冠商文本', 'partner.apply_crown_text', 'String', '', 'sys', 1, '线下经销商申请成为皇冠商的描述文本，可输入\\n表示换行', 0),
 (400, '授权线下经销商文本', 'partner.create_physical_seller_text', 'String', '', 'sys', 1, '线下经销商申请成为皇冠商的描述文本，可输入\\n表示换行', 0),
 (400, '新晋线下皇冠商进货时间', 'partner.new_physical_seller_wholesale_time', 'int', '4', '', 1, '线下经销商被批准成为线下皇冠商后，必须在规定小时内完成一定额的批发订单的时间，单位小时。', 0),
 (400, '新晋线下皇冠商最小进货额', 'partner.new_physical_seller_wholesale_amount', 'int', '2000', '', 1, '线下经销商被批准成为线下皇冠商后，必须在规定小时内完成一定额的批发批发订单的最小额度，单位元。', 0),
 (400, '线下皇冠商申请自动审核开关', 'partner.auto_audit_physical_crown', 'boolean', 'true', 'sys', 1, '线下皇冠商申请自动审核开关', 0),
 (400, '线下皇冠商申请自动审核次数', 'partner.auto_audit_physical_crown_times', 'int', '3', 'sys', 1, '当自动审核次数达到指定次数后，需要转人工审核', 0);


