DROP TABLE IF EXISTS t_pcd;

CREATE TABLE IF NOT EXISTS t_pcd (
  id integer not null primary key auto_increment,
  name varchar(50) not null,
  type varchar(50) not null,
  parent_id integer,
  foreign key (parent_id) references t_pcd (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
