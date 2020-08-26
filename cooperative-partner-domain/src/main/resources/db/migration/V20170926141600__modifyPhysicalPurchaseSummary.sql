/*上上级当月期望提成额，不是自己拿而是上上级拿。计算方法：从日志表中把该用户当月的期望加起来，然后乘以上上级分成比例*/
alter table t_physical_purchase_summary add column monthly_expected_settled_amount_lv2 decimal(15,2) not null default 0.00;

/*上级预期提成金额，不是真正的提成金额，真正提成金额与下线有关*/
alter table t_physical_purchase_journal add column expected_reward_lv1 decimal(10,2) not null default 0.00;

/*上上级预期提成金额，不是真正的提成金额，真正提成金额与下线有关*/
alter table t_physical_purchase_journal add column expected_reward_lv2 decimal(10,2) not null default 0.00;

/*上级预期提成金额，不是真正的提成金额，真正提成金额与下线有关*/
alter table t_physical_purchase_journal add column settlement_proportion_lv1 decimal(10,2) not null default 0.00;

/*上上级预期提成金额，不是真正的提成金额，真正提成金额与下线有关*/
alter table t_physical_purchase_journal add column settlement_proportion_lv2 decimal(10,2) not null default 0.00;

alter table t_physical_purchase_journal modify product_settlement_proportion decimal(10,2) not null default 0.00;

update t_physical_purchase_journal set expected_reward_lv1 = expected_reward, settlement_proportion_lv1 = product_settlement_proportion;
