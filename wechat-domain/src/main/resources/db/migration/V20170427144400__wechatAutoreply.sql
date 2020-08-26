DROP TABLE IF EXISTS t_wechat_keyword_autoreply_item;
DROP TABLE IF EXISTS t_wechat_keyword_autoreply;
DROP TABLE IF EXISTS t_wechat_message_autoreply;
DROP TABLE IF EXISTS t_wechat_subscribe_autoreply;


CREATE TABLE IF NOT EXISTS t_wechat_subscribe_autoreply (
 id integer primary key auto_increment,
 type varchar(50) not null,
 title varchar(200),
 digest varchar(200),
 create_time timestamp null,
 content text,
 url varchar(255),
 thumb_url varchar(255),
 enabled integer default 1
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wechat_message_autoreply (
 id integer primary key auto_increment,
 type varchar(50) not null,
 title varchar(200),
 digest varchar(200),
 create_time timestamp null,
 content text,
 url varchar(255),
 thumb_url varchar(255),
 enabled integer default 1
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wechat_keyword_autoreply (
 id integer primary key auto_increment,
 name varchar(50),
 keyword varchar(50),
 enabled integer default 1
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wechat_keyword_autoreply_item (
 id integer primary key auto_increment,
 keyword_id integer not null,
 type varchar(50) not null,
 title varchar(200),
 digest varchar(200),
 create_time timestamp null,
 content text,
 url varchar(255),
 thumb_url varchar(255),
 enabled integer default 1,
 foreign key (keyword_id) references t_wechat_keyword_autoreply (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;