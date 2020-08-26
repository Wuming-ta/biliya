DROP TABLE IF EXISTS t_order_express;

CREATE TABLE IF NOT EXISTS t_order_express (
  id integer not null primary key auto_increment,
  order_id integer not null,
  mid integer,
  create_date datetime default null,
  express_company varchar(200),
  express_code varchar(200),
  express_number varchar(200),
  order_items text,
  foreign key (order_id) references t_order (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

alter table t_order_item add column mid integer;


update t_order_item a set a.mid = (select mid from t_product b where b.id = a.product_id);

