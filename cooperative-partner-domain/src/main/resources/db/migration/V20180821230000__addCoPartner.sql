DROP TABLE IF EXISTS t_copartner;
CREATE TABLE IF NOT EXISTS t_copartner (
    id integer not null primary key auto_increment,
    seller_id integer,
    create_time timestamp null,
    status varchar(50) default null, /*  */
    foreign key (seller_id) references t_seller (id) on delete cascade
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

DROP TABLE IF EXISTS t_copartner_relation;
CREATE TABLE IF NOT EXISTS t_copartner_relation (
    id integer not null primary key auto_increment,
    copartner_id integer,
    seller_id integer,
    create_time timestamp null,
    foreign key (seller_id) references t_seller (id) on delete cascade,
    foreign key (copartner_id) references t_copartner (id) on delete cascade
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

DROP TABLE IF EXISTS t_copartner_settlement;
CREATE TABLE IF NOT EXISTS t_copartner_settlement (
    id integer not null primary key auto_increment,
    copartner_id integer,
    statistic_month date null, /*统计月份, 2016-12-01*/
    settled_amount decimal(15, 2) not null default 0.00,
    settlement_proportion decimal(5, 2) not null default 0.00, /*分成比例*/
    transferred integer not null default 0, /*是否已转到积分系统*/
    transferred_amount decimal(15, 2) not null default 0.00, /*转移到积分系统的数量，结果是乘以转换率后的*/
    foreign key (copartner_id) references t_copartner (id) on delete cascade
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;



INSERT INTO t_settlement_proportion (name, type, proportion, `level`) VALUES
('合伙人', 'COPARTNER', '{"percentage": true, "value": 2.0}', 0);
