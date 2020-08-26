DROP TABLE IF EXISTS t_feedback;
DROP TABLE IF EXISTS t_faq;
DROP TABLE IF EXISTS t_about_mall;
DROP TABLE IF EXISTS t_ad_link_definition;
DROP TABLE IF EXISTS t_ad;
DROP TABLE IF EXISTS t_ad_group;
DROP TABLE IF EXISTS t_customer_service_type;
DROP TABLE IF EXISTS t_kf_qq;
DROP TABLE IF EXISTS t_system_announcement;

CREATE TABLE IF NOT EXISTS t_feedback (
    id integer not null primary key auto_increment,
    user_id integer not null,
    content text,
    created_date timestamp null,
    unread integer not null default 1,
    foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_feedback_image (
    id integer not null primary key auto_increment,
    feedback_id integer not null,
    url varchar(200)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_faq_type (
    id integer not null primary key auto_increment,
    name varchar(50)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_faq (
    id integer not null primary key auto_increment,
    type_id integer not null,
    title varchar(50),
    content text,
    created_date  timestamp null,
    last_modified_date  timestamp null,
    foreign key (type_id) references t_faq_type (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_about_mall (
    id integer not null primary key auto_increment,
    image varchar(50),
    content text
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_ad_group (
    id integer not null primary key auto_increment,
    name varchar(50) not null unique
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_ad (
    id integer not null primary key auto_increment,
    group_id integer not null,
    name varchar(100),
    image varchar(200),
    type varchar(100),
    enabled integer not null default 1,
    target_url varchar(200),
    strategy varchar(200),
    foreign key (group_id) references t_ad_group (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_ad_link_definition (
    id integer not null primary key auto_increment,
    type integer default 0, /*0 功能链接, 1 产品链接*/
    name varchar(100),
    url varchar(200)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS t_customer_service_type (
  id integer not null primary key auto_increment,
  name varchar(100)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_kf_qq (
  id integer not null primary key auto_increment,
  name varchar(100),
  number varchar(100),
  enabled integer not null default 1
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_system_announcement (
  id integer not null primary key auto_increment,
  name varchar(100),
  content text,
  created_date timestamp null,
  last_modified_date timestamp null,
  enabled integer default 0
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

insert into t_ad_link_definition (type, name, url) values
(0, '首页', '#/home/homePage'),
(0, '购物车', '#/home/cart'),
(0, '分类', '#/home/category'),
(0, '个人中心', '#/home/my'),
(0, '销售中心', '#/home/sellerPage'),
(0, '订单中心', '#/order/all'),
(0, '10元区', '#/goodsList/10'),
(1, '产品', '#/details/'),
(2, '类别', '#/home/category/');

