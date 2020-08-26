
insert into t_notification (identifier, name, title, icon, badge_url, url, display_mode, timeout) values
('notification.new_order',
 '新订单',
 '新订单',
 '<i class="fa fa-sun-o fa-lg" aria-hidden="true"></i>',
 '/order/newOrderCount',
 '/order?statuses=PAID_CONFIRM_PENDING&statuses=CONFIRMED_DELIVER_PENDING',
 1,
 60
);

