DROP TABLE IF EXISTS `t_trial_application`;
DROP TABLE IF EXISTS `t_trial_image`;
DROP TABLE IF EXISTS `t_trial`;

CREATE TABLE IF NOT EXISTS `t_trial` (  /*试用活动表*/
  `id` INT NOT NULL AUTO_INCREMENT,
  `product_id` INT NOT NULL COMMENT '产品id',
  `price` DECIMAL(10,2) DEFAULT 0 COMMENT '价格',
  `name` VARCHAR(100) COMMENT '试用装名称',
  `short_note` VARCHAR(50) COMMENT '简短描述',
  `enabled` INT DEFAULT 0 COMMENT '是否启用', /*0 不启用 1 启用*/
  `start_time` DATETIME DEFAULT NULL COMMENT '有效申请时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `cover` varchar(255) COMMENT '试用装封面',
  `note` TEXT COMMENT '描述',
  `index` INT DEFAULT 100 COMMENT '排序号',
  `shipping_type` INT DEFAULT 0 COMMENT '运费支付 0 商家 1 顾客',
  `payment_type` varchar(50) COMMENT '支付方式',

   /*此版本号是考虑用户是否是重复申领的其中一个考虑因素。
    *比如，商家可能会在2018年1月~3月给予用户试用装A的试用资格，活动结束之后，在2019年1月~3月又给予用户试用装A的试用资格。
	*则这两次的试用是不同的版本，在试用申请表中记录了试用活动的版本号，对于某个用户，若有2018年1月~3月的试用记录，则他在2019年也
	*是可以继续试用的。
	*/
  `version` INT DEFAULT 1 COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS t_trial_image (
  id integer not null primary key auto_increment,
  trial_id integer not null,
  url varchar(200) not null,
  sort_order int default 1,
  foreign key (trial_id) references t_trial (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS `t_trial_application` ( /*试用装申请表*/
  `id` INT NOT NULL AUTO_INCREMENT,
  `trial_id` INT NOT NULL COMMENT '试用活动id',
  `order_id` INT COMMENT '订单id', /*关联的订单*/
  `order_number` varchar(50) COMMENT '订单号', /*关联的订单*/
  `user_id` INT NOT NULL COMMENT '申请人id',
  `created_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `status` VARCHAR(20) COMMENT '状态',  /*AUDITING 申请中 DELIVER_PENDING 待发货 DELIVERING 发货中 DELIVERED 已发货 REJECTED 未获得试用资格  */
  `shipping_type` INT DEFAULT 0 COMMENT '0 包邮 1 根据产品计算',
  `note` TEXT COMMENT '描述', /*如：未获得试用资格时，后台人员填写原因*/
  `version` INT DEFAULT 1 COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;