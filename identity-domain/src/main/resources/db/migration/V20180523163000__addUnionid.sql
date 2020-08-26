ALTER TABLE t_user ADD COLUMN wx_unionid VARCHAR(50);
ALTER TABLE t_user ADD COLUMN wxa_openid VARCHAR(50);
ALTER TABLE t_user ADD COLUMN wxapp_openid VARCHAR(50);
ALTER TABLE t_user ADD CONSTRAINT wx_unionid UNIQUE(wx_unionid);
ALTER TABLE t_user ADD CONSTRAINT wxa_openid UNIQUE(wxa_openid);
ALTER TABLE t_user ADD CONSTRAINT wxapp_openid UNIQUE(wxapp_openid);

/*区分后台用户和终端用户, 1 终端用户, 0 后台用户*/
ALTER TABLE t_user ADD COLUMN app_user int default 0;

/*微信昵称*/
ALTER TABLE t_user ADD COLUMN wechat_name varchar(50) default null;

/*会员级别*/
ALTER TABLE t_user ADD COLUMN grade varchar(50) default null;


ALTER TABLE t_user ADD COLUMN contact_phone varchar(50) default null comment '员工联系电话';
ALTER TABLE t_user ADD COLUMN contact_wx_number varchar(50) default null comment '员工联系微信号';

