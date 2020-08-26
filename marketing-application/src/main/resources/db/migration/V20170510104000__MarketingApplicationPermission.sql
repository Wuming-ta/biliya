INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.piece.view', 'Marketing Piece View', 'View marketing piece.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.piece.edit', 'Marketing Piece Edit', 'Edit marketing piece.');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.piece.delete', 'Marketing Piece Delete', 'Delete marketing piece.');

INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.wholesale.view', 'Marketing Wholesale View', 'View marketing wholesale');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.wholesale.edit', 'Marketing Wholesale Edit', 'Edit marketing wholesale');
INSERT INTO t_permission_definition (identifier, name, description)
 values ('marketing.wholesale.delete', 'Marketing Wholesale Delete', 'Delete marketing wholesale');


INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('marketing.piece.mgmt', 'Marketing Piece Management', 'Manage the marketing piece.', 'marketing.piece.view|marketing.piece.edit|marketing.piece.delete');
INSERT INTO t_permission_group_definition (identifier, name, description, permissions)
 values ('marketing.wholesale.mgmt', 'Marketing Wholesale Management', 'Manage the marketing wholesale.', 'marketing.wholesale.view|marketing.wholesale.edit|marketing.wholesale.delete');

insert into t_permission (role_id, identifier) values (1, 'marketing.piece.view');
insert into t_permission (role_id, identifier) values (1, 'marketing.piece.edit');
insert into t_permission (role_id, identifier) values (1, 'marketing.piece.delete');

insert into t_permission (role_id, identifier) values (1, 'marketing.wholesale.view');
insert into t_permission (role_id, identifier) values (1, 'marketing.wholesale.edit');
insert into t_permission (role_id, identifier) values (1, 'marketing.wholesale.delete');

