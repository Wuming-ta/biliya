<#macro header>
<nav class="navbar navbar-default navbar-cls-top " role="navigation" style="margin-bottom: 0">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".sidebar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="${base}/">${productName!'JFEAT'}</a>
    </div>
    <@shiro.user>
    <div class="btn-group pull-right">
      <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" style="color: white;">
        <span class="glyphicon glyphicon-user"></span> 
        <span class="caret"></span>
      </a>
      <ul class="dropdown-menu">
        <li><a href="${base}/profile">${_res.get("header.profile")}</a></li>
        <li><a href="${base}/auth/logout">${_res.get("header.logout")}</a></li>
      </ul>
    </div>
    </@shiro.user>
</nav>
</#macro>
