DROP TABLE IF EXISTS t_event_log;

CREATE TABLE IF NOT EXISTS t_event_log  (
  id integer not null primary key auto_increment,
  name varchar(255),
  user varchar(255),
  user_agent varchar(255),
  ip varchar(100),
  create_time datetime default null,
  event_type varchar(100),
  data text
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
