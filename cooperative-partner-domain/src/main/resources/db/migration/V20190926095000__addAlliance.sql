DROP TABLE IF EXISTS t_alliance;

CREATE TABLE IF NOT EXISTS t_alliance (
    id integer not null primary key auto_increment,
    user_id integer not null,
    invitor_alliance_id integer comment '邀请人',
    alliance_ship integer not null default 0 comment '是否为盟友 0:否，1:临时，2:正式',
    stockholder_ship integer not null default 0 comment '是否为股东 0:否，1:是',
    creation_time timestamp null,
    alliance_ship_time timestamp null comment '成为盟友的时间',
    stockholder_ship_time timestamp null comment '成为股东的时间',
    temp_alliance_expiry_time timestamp null comment '临时盟友过期时间',
    foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
