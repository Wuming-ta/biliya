<#macro header>
<#include "notification.ftl"/>
<nav class="navbar navbar-default navbar-cls-top navbar-fixed-top" role="navigation" style="margin-bottom: 0">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".sidebar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="${base}/">${productName!'JFEAT'}</a>
    </div>
    <div class="pull-left" style="color: white; margin-top: 15px;margin-left: 10px">
        <@notification />
    </div>
    <@shiro.user>
    <div class="btn-group pull-right" style="color: white; margin-top: 18px;margin-right: 10px">
        <i class="fa fa-user" aria-hidden="true"></i> <@shiro.principal property="name" />, ${_res.get("header.welcome")}
        <a style="color: white" href="${base}/auth/logout"><i class="fa fa-sign-out" aria-hidden="true"></i> ${_res.get("header.logout")}</a>
    </div>
    </@shiro.user>
</nav>
</#macro>
