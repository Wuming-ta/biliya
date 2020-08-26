<#macro header>
<#include "notification.ftl"/>
<style>
body{padding-top: 60px;}
</style>
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
</nav>
</#macro>
