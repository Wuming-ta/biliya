DROP TABLE IF EXISTS t_settled_term;
DROP TABLE IF EXISTS t_settled_merchant_introduction;
DROP TABLE IF EXISTS t_settled_merchant_approve_log;
DROP TABLE IF EXISTS t_merchant_product_category;
DROP TABLE IF EXISTS t_merchant_message ;
DROP TABLE IF EXISTS t_settled_merchant;
DROP TABLE IF EXISTS t_settled_merchant_type;
DROP TABLE IF EXISTS t_merchant_config;


CREATE TABLE IF NOT EXISTS t_settled_term (
  id integer primary key auto_increment,
  name varchar(200),
  content text
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_settled_merchant_type (
  id integer primary key auto_increment,
  name varchar(200) not null,
  product_count integer,
  deposit decimal(10,2)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_settled_merchant (
    id integer primary key auto_increment,
    type_id integer,
    name varchar(200) not null,
    description varchar(255),
    logo varchar(255),
    address varchar(255),
    phone varchar(50) not null,
    contact_user varchar(50) not null,
    contact_phone varchar(50) not null,
    contact_email varchar(50),
    id_number varchar(50),
    id_front varchar(255),
    id_back varchar(255),
    business_license_number varchar(200),
    business_license_image varchar(200),
    status varchar(50),
    created_date timestamp null,
    approved_date timestamp null,
    quality_ranking integer,
    attitude_ranking integer,
    express_ranking integer,
    foreign key (type_id) references t_settled_merchant_type (id) on delete set null
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_settled_merchant_approve_log (
    id integer primary key auto_increment,
    merchant_id integer not null,
    handled_date timestamp null,
    result varchar(255),
    administrator varchar(255),
    foreign key (merchant_id) references t_settled_merchant (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_user_settled_merchant (
    user_id integer not null,
    merchant_id integer not null,
    foreign key (user_id) references t_user (id) on delete cascade,
    foreign key (merchant_id) references t_settled_merchant (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_settled_merchant_introduction (
    id integer primary key auto_increment,
    merchant_id integer unique not null ,
    introduction text,
    foreign key (merchant_id) references t_settled_merchant (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_merchant_product_category (
 id integer primary key auto_increment,
 merchant_id integer not null,
 name varchar(50) not null,
 cover varchar(255),
 visible integer not null default 0,
 sort_order integer,
 foreign key (merchant_id) references t_settled_merchant (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_merchant_message (
 id integer primary key auto_increment,
 merchant_id integer not null,
 created_date timestamp null,
 unread integer not null default 1,
 title varchar(100),
 content text,
  foreign key (merchant_id) references t_settled_merchant (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

