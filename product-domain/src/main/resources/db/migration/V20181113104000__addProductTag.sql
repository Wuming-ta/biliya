DROP TABLE IF EXISTS t_product_tag;
DROP TABLE IF EXISTS t_product_tag_relation;

CREATE TABLE IF NOT EXISTS t_product_tag (
    id integer not null primary key auto_increment,
    identifier varchar(50) not null comment '标签标识',
    name varchar(50) not null comment '标签名称',
    sort_order integer default 1 comment '排序号',
    unique(identifier)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_tag_relation (
    id integer not null primary key auto_increment,
    tag_id integer not null,
    product_id integer not null,
    foreign key (tag_id) references t_product_tag (id) on delete cascade,
    foreign key (product_id) references t_product (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
