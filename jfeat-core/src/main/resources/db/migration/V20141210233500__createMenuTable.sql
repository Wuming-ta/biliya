DROP TABLE IF EXISTS t_menu;

CREATE TABLE IF NOT EXISTS t_menu  (
  id integer not null primary key auto_increment,
  name varchar(255),
  url varchar(255),
  sort_order integer not null default 1,
  parent_id integer default null,
  foreign key (parent_id) references t_menu (id)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

