DROP TABLE IF EXISTS t_user_join_notify;

CREATE TABLE IF NOT EXISTS t_user_join_notify  (
  id integer not null primary key auto_increment,
  user_id integer not null,
  join_time datetime default null,
  is_read smallint default 0,
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

alter table t_user add column cabin_code varchar(50) default null comment '关注小屋';
alter table t_user add column store_code varchar(50) default null comment '关注门店';
alter table t_user add column assistant_code varchar(50) default null comment '导购员';
