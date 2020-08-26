
insert into t_settled_merchant (id, type_id, name, phone, contact_user, contact_phone, status) values
(1, 1, '自营', '', '', '', 'APPROVED');

insert into t_user_settled_merchant (user_id, merchant_id) values (1, 1);
insert into t_settled_merchant_introduction (id, merchant_id, introduction) values (1, 1, '');
