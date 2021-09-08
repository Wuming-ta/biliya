DROP TABLE IF EXISTS t_notification;

CREATE TABLE IF NOT EXISTS t_notification  (
  id integer not null primary key auto_increment,
  identifier varchar(100) not null, /*用于在privilege查找*/
  name varchar(255),
  title varchar(255),
  icon varchar(100), /*http://fontawesome.io 定义的icon*/
  display_mode integer default 0, /*0: 名字, 1:icon*/
  badge_url varchar(255), /*通过ajax方式拿通知数量的地址*/
  url varchar(255), /*点击后跳到的地址*/
  timeout integer default 60 /*超时去url拿新数据,单位秒*/
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

