DROP TABLE IF EXISTS t_product_settlement_proportion;

CREATE TABLE IF NOT EXISTS t_product_settlement_proportion (
  id integer not null primary key auto_increment,
  product_id integer not null,
  name varchar(50),
  type varchar(50),
  proportion text, /*利润分成, json format*/
  level integer,
  foreign key (product_id) references t_product (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;