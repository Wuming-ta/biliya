

alter table t_coupon_strategy add column target_type integer default 0;
alter table t_coupon_strategy add column status varchar(50) default 'DRAFT';
alter table t_coupon_strategy add column description text;
alter table t_coupon_strategy add column target_condition text;
alter table t_coupon_strategy add column start_time datetime default null;
alter table t_coupon_strategy add column end_time datetime default null;
alter table t_coupon_strategy add column version integer default 0;

CREATE TABLE IF NOT EXISTS t_coupon_strategy_taken_record (
  id integer not null primary key auto_increment,
  strategy_id integer not null,
  user_id integer not null,
  version integer default 0,
  created_date datetime default null,
  foreign key (strategy_id) references t_coupon_strategy (id) on delete cascade,
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

