INSERT INTO t_permission_definition(identifier,name,description)
VALUES ('wechat.view','Wechat Config View','View the Wechat Config information.');
INSERT INTO t_permission_definition(identifier,name,description)
VALUES ('wechat.edit','Wechat Config Edit','Edit the Wechat Config information.');
INSERT INTO t_permission_definition(identifier,name,description)
VALUES ('wechat.delete','Wechat Config Delete','Delete the Wechat Config information.');
INSERT INTO t_permission_group_definition(identifier,name,description,permissions)
VALUES ('wechat.mgmt','Wechat Config Management','Manage the Wechat Config information.','wechat.view|wechat.edit|wechat.delete');

insert into t_permission (role_id,identifier) values (1,'wechat.view');
insert into t_permission (role_id,identifier) values (1,'wechat.edit');
insert into t_permission (role_id,identifier) values (1,'wechat.delete');

