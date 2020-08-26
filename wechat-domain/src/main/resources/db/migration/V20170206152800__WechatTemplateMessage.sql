DROP TABLE IF EXISTS t_wechat_field;
DROP TABLE IF EXISTS t_wechat_message_type_prop;
DROP TABLE IF EXISTS t_wechat_template_message;
DROP TABLE IF EXISTS t_wechat_message_type;


CREATE TABLE IF NOT EXISTS t_wechat_message_type (
    id integer primary key auto_increment,
    name varchar(50) not null,
    display_name varchar(50)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wechat_message_type_prop (
    id integer primary key auto_increment,
    type_id integer not null,
    name varchar(50) not null,
    display_name varchar(50),
    display_value varchar(200) default null,
    foreign key (type_id) references t_wechat_message_type (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wechat_template_message (
    id integer primary key auto_increment,
    template_id varchar(200) not null,
    type_id integer not null,
    name varchar(200) not null,
    enabled integer not null default 0,
   foreign key (type_id) references t_wechat_message_type (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wechat_field (
    id integer primary key auto_increment,
    prop_id integer not null,
    template_message_id integer not null,
    name varchar(50) not null,
    foreign key (prop_id) references t_wechat_message_type_prop (id) on delete cascade,
    foreign key (template_message_id) references t_wechat_template_message (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;
