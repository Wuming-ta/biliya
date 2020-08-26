insert into t_settled_term (id, name, content) values
(1, '商家入驻协议', null);

insert into t_settled_merchant_type (id, name, product_count, deposit) values
(1, '默认', 99999, 0);



insert into t_config_group (id, name) values (500, '商家配置');

insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
 (500, '是否允许平台零售', 'merchant.allow_platform_sale', 'boolean', 'true', 'sys', 1, '是否允许平台零售, 如果设为不允许，则只有进入指定的商家店铺才可以下单'),
 (500, '是否需要营业执照', 'merchant.license_req', 'boolean', 'true', '', 1, '是否需要营业执照'),
 (500, '是否需要身份证', 'merchant.idcard_req', 'boolean', 'true', '', 1, '是否需要身份证'),
 (500, '是否需要保证金', 'merchant.deposit_req', 'boolean', 'true', '', 1, '是否需要保证金'),
 (500, '是否允许自主申请', 'merchant.allow_apply', 'boolean', 'false', '', 1, '是否允许用户自主申请成为商家');
