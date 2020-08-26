DROP TABLE IF EXISTS t_physical_seller;
DROP TABLE IF EXISTS t_physical_purchase_journal;
DROP TABLE IF EXISTS t_agent_summary;
DROP TABLE IF EXISTS t_physical_purchase_summary;
DROP TABLE IF EXISTS t_physical_settlement_proportion;
DROP TABLE IF EXISTS t_physical_settlement_definition;

/*线下经销商*/
CREATE TABLE IF NOT EXISTS t_physical_seller (
    id integer not null primary key auto_increment,
    seller_id integer not null unique,
    parent_seller_id integer,
    total_amount decimal(15, 2) not null default 0.00, /*总入货额*/
    total_settled_amount decimal(15, 2) not null default 0.00, /*总提成额*/
    created_date timestamp null,
    province varchar(50),
    city varchar(50),
    district varchar(50),
    latest_bonus_date date null, /*最后一次生成年终奖的日期*/
    foreign key (seller_id) references t_seller (id) on delete cascade,
    foreign key (parent_seller_id) references t_seller (id) on delete set null
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

/*入货明细*/
CREATE TABLE IF NOT EXISTS t_physical_purchase_journal (
    id integer not null primary key auto_increment,
    seller_id integer not null,
    amount decimal(15, 2) not null default 0.00,
    note text,
    created_date timestamp null,
    order_number varchar(50),
    order_id integer,
    order_item_id integer,
    product_name varchar(255),
    product_settlement_proportion integer, /*产品分成比例*/
    expected_reward decimal(10,2), /*预期提成金额，不是真正的提成金额，真正提成金额与下线有关*/
    foreign key (seller_id) references t_seller (id) on delete cascade
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

/*入货汇总*/
CREATE TABLE IF NOT EXISTS t_physical_purchase_summary (
    id integer not null primary key auto_increment,
    seller_id integer not null,
    monthly_amount decimal(15, 2) not null default 0.00, /*当月总额*/
    monthly_settled_amount decimal(15, 2) not null default 0.00, /*当月提成额，仅用于前端展示，并不等于当月期望提成额*分成比例
                                                                   ，而是结合下线来计算的*/
    monthly_expected_settled_amount decimal(15, 2) not null default 0.00, /*当月期望提成额，即从日志表中把该用户当月的期望加起来*/
    statistic_month date null, /*统计月份, 2016-12-01*/
    settlement_proportion decimal(5, 2) not null default 0.00, /*分成比例*/
    transferred integer not null default 0, /*是否已转到积分系统*/
    transferred_amount decimal(15, 2) not null default 0.00, /*转移到积分系统的数量，结果是乘以转换率后的*/
    foreign key (seller_id) references t_seller (id) on delete cascade
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_agent_summary ( /*记录“提成”和“年终奖”，年终奖采用顺延年 */
    id integer not null primary key auto_increment,
    seller_id integer not null,
    statistic_month date null, /*统计月份, 2016-12-01*/
    end_month date null, /*结束月份，2016-12-31（主要用于记录奖金：每年1月1号1点计算上1年销售额以计算奖金）*/
    pcd_id integer not null,
    amount decimal(15,2) not null default 0.00,
    year_statistic_amount decimal(15,2) not null default 0.00, /*年累计订单金额（从上次结算奖金日期（即t_physical_seller
                                            的latest_latest_bonus_date，如果为null，则看created_date）开始计算，
                                            到此statistic_month之间(含）的订单金额），此字段用于“提成计算程序”而不是“奖金计算程序”*/
    settled_amount decimal(15, 2) not null default 0.00,
    settlement_proportion decimal(5, 2) not null default 0.00, /*分成比例*/
    transferred integer not null default 0, /*是否已转到积分系统*/
    transferred_amount decimal(15, 2) not null default 0.00, /*转移到积分系统的数量，结果是乘以转换率后的*/
    foreign key (seller_id) references t_seller (id) on delete cascade
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

/*分成比例*/
CREATE TABLE IF NOT EXISTS t_physical_settlement_proportion (
    id integer not null primary key auto_increment,
    min_amount decimal(15, 2) not null default 0.00,
    max_amount decimal(15, 2) not null default 0.00,
    percentage decimal(5, 2) not null default 0.00
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


/*提成配额*/
CREATE TABLE IF NOT EXISTS t_physical_settlement_definition (
    id integer not null primary key auto_increment,
    amount decimal(15, 2) not null default 0.00
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

--线下皇冠商提成配额,表示他最多可以提成的金额, 默认1000元
INSERT INTO t_physical_settlement_definition (amount) VALUES (1000.0);

--设置分成比例
INSERT INTO t_physical_settlement_proportion (min_amount, max_amount, percentage) VALUES
(1000, 5000, 2.0),
(5001, 10000, 5.0),
(10001, 50000, 6.0),
(50001, -1, 8.0);


