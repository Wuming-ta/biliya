
alter table t_member_ext add column total_credit integer default 0 comment '累计积分';
alter table t_member_ext add column credit integer default 0 comment '可用积分';
alter table t_member_ext add column be_member_time datetime default null comment '成为会员时间';
alter table t_member_ext add column consume_amount integer default 0 comment '累计消费金额';
alter table t_member_ext add column consume_count integer default 0 comment '累计消费次数';
alter table t_member_ext add column last_consume_time datetime default null comment '最后消费时间';
