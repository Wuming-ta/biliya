DROP TABLE IF EXISTS t_agent_purchase_journal;
CREATE TABLE IF NOT EXISTS t_agent_purchase_journal ( /*线下代理商针对每个OrderItem的分成明细表*/
 id integer not null primary key auto_increment,
 seller_id integer not null, /*代理商sellerid*/
 create_date datetime null, /*生成此记录的时间*/
 pcd_id integer not null,
 pcd_name varchar(50),
 order_item_id integer not null,
 product_id integer not null,
 product_name varchar(100),
 product_specification_name varchar(50),
 product_cover varchar(200),
 price decimal(15,2), /*该orderItem的单价*/
 quantity integer, /*该orderItem的数量*/
 final_price decimal(15,2), /*该orderItem的final_price*/
 marketing_id integer not null,
 marketing_name varchar(50),
 percentage decimal(15, 2), /*某seller代理某地区的提成比例*/
 agent_proportion decimal(15, 2), /*某个批发活动的代理提成比例*/
 settled_amount decimal(15, 2) not null default 0.00, /* 最终提成 = final_price * quantity * percentage * agent_proportion /100  */
 order_user_id integer, /*下此订单的人的userid*/
 order_user_name varchar(50) /*下此订单的人的username*/
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;