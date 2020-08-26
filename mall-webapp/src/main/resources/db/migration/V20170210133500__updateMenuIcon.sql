update t_menu set icon='<i class="fa fa-home fa-fw" aria-hidden="true"></i>' where name='menu.home';
update t_menu set icon='<i class="fa fa-id-badge fa-fw" aria-hidden="true"></i>' where name='menu.identity';
update t_menu set icon='<i class="fa fa-shopping-bag fa-fw" aria-hidden="true"></i>' where name='menu.product_mgmt';
update t_menu set icon='<i class="fa fa-cubes fa-fw" aria-hidden="true"></i>' where name='menu.order_mgmt';
update t_menu set icon='<i class="fa fa-handshake-o fa-fw" aria-hidden="true"></i>' where name='menu.cooperative-partner';
update t_menu set icon='<i class="fa fa-jpy fa-fw" aria-hidden="true"></i>' where name='menu.settlement';
update t_menu set icon='<i class="fa fa-wrench fa-fw" aria-hidden="true"></i>' where name='menu.config';
update t_menu set icon='<i class="fa fa-info-circle fa-fw" aria-hidden="true"></i>' where name='menu.event_log';
update t_menu set icon='<i class="fa fa-user-circle-o fa-fw" aria-hidden="true"></i>' where name='menu.member_mgmt';
update t_menu set icon='<i class="fa fa-cogs fa-fw" aria-hidden="true"></i>' where name='menu.misc-config';
update t_menu set icon='<i class="fa fa-weixin fa-fw" aria-hidden="true"></i>' where name='menu.wechat_mgmt';

update t_notification set icon='<i class="fa fa-ticket fa-lg" aria-hidden="true"></i>' where identifier='notification.withdraw_apply';
update t_notification set icon='<i class="fa fa-tree fa-lg" aria-hidden="true"></i>' where identifier='notification.return_order';
update t_notification set icon='<i class="fa fa-street-view fa-lg" aria-hidden="true"></i>' where identifier='notification.seller_apply';
