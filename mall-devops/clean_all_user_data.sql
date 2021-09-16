-- 清空用户数据

DELETE FROM `alliance`.t_user WHERE wxa_openid IS NOT NULL -- 微信小程序用户
DELETE FROM `alliance`.t_order  -- 订单
DELETE FROM `alliance`.t_withdraw_account	 -- 线下提现
DELETE FROM `alliance`.t_offline_withdrawal -- 提现申请
DELETE FROM `alliance`.t_alliance	-- 盟友管理
DELETE FROM `alliance`.t_reward_cash -- 奖励总汇
DELETE FROM `alliance`.t_wallet_history -- 奖金历史
DELETE FROM `alliance`.t_order_item_reward  -- 结算记录
DELETE FROM `alliance`.t_product --产品
DELETE FROM `alliance`.t_product_category	-- 产品类别
DELETE FROM `alliance`.t_operation_log	-- 操作日志
DELETE FROM `alliance`.t_product_brand	-- 品牌管理

-- 备份用户数据
INSERT INTO `alliance`.t_user SELECT * FROM `alliance_v1_no_sensitive`.t_user  WHERE wxa_openid IS NOT NULL
INSERT INTO `alliance`.t_order SELECT * FROM `alliance_v1_no_sensitive`.t_order
INSERT INTO `alliance`.t_withdraw_account SELECT * FROM `alliance_v1_no_sensitive`.t_withdraw_account
INSERT INTO `alliance`.t_offline_withdrawal SELECT * FROM `alliance_v1_no_sensitive`.t_offline_withdrawal
INSERT INTO `alliance`.t_alliance SELECT * FROM `alliance_v1_no_sensitive`.t_alliance
INSERT INTO `alliance`.t_reward_cash SELECT * FROM `alliance_v1_no_sensitive`.t_reward_cash
INSERT INTO `alliance`.t_wallet_history SELECT * FROM `alliance_v1_no_sensitive`.t_wallet_history
INSERT INTO `alliance`.t_order_item_reward SELECT * FROM `alliance_v1_no_sensitive`.t_order_item_reward
INSERT INTO `alliance`.t_product SELECT * FROM `alliance_v1_no_sensitive`.t_product
INSERT INTO `alliance`.t_product_category SELECT * FROM `alliance_v1_no_sensitive`.t_product_category 
INSERT INTO `alliance`.t_operation_log SELECT * FROM `alliance_v1_no_sensitive`.t_operation_log 
INSERT INTO `alliance`.t_product_brand SELECT * FROM `alliance_v1_no_sensitive`.t_product_brand 
