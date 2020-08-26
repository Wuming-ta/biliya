/*商家的订单通过分成的方式返回给商家*/
DROP TABLE IF EXISTS t_settled_merchant_settlement_proportion;

CREATE TABLE IF NOT EXISTS t_settled_merchant_settlement_proportion (
    id integer not null primary key auto_increment,
    percentage decimal(5, 2) not null default 100.00
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

insert into t_settled_merchant_settlement_proportion (percentage) values (100.0);
