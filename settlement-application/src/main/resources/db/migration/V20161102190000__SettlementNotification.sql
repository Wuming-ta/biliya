
insert into t_notification (identifier, name, title, icon, badge_url, url, display_mode, timeout) values
('notification.withdraw_apply',
 '提现申请',
 '有新的提现申请,等待处理',
 '<i class="fa fa-money fa-lg" aria-hidden="true"></i>',
 '/reward_cash/countApplying',
 '/reward_cash?status=APPLYING',
 1,
 90
);

