DROP TABLE IF EXISTS t_widget;

CREATE TABLE IF NOT EXISTS t_widget  (
  id integer not null primary key auto_increment,
  name varchar(255),
  url varchar(255),
  width integer not null default 12,
  sort_order integer not null default 1
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

