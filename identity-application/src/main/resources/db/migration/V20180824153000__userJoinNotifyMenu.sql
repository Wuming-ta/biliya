insert into t_menu (id, name, url, sort_order, parent_id) values (103, 'menu.user_join_notify', 'user_join_notify', 3, 100);
insert into t_menu (id, name, url, sort_order, parent_id) values (111, 'menu.staff', 'staff', 3, 100);


insert into t_notification (identifier, name, title, icon, badge_url, url, display_mode, timeout) values
('notification.user_notify_unread_count',
 '新用户通知',
 '新用户通知',
 '<i class="fa fa-id-badge fa-lg" aria-hidden="true"></i>',
 '/user_join_notify/unreadCount',
 '/user_join_notify?isRead=0',
 1,
 60
);

