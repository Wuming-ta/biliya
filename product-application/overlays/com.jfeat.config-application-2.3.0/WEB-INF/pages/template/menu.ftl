<#macro menu>
<#include "menuItem.ftl"/>
<nav class="navbar-side" role="navigation">
    <div class="sidebar-collapse">
        <ul class="nav" id="main-menu">
            <@shiro.user>
            <#if currentUser?? && currentUser.avatar?? && currentUser.avatar != ''>
                <#assign avatar = currentUser.avatar />
            <#else>
                <#assign avatar>${base}/assets/img/find_user.png</#assign>
            </#if>
            <li class="text-center">
                <a href="${base}/profile"><img src="${avatar}" class="user-image img-responsive"/></a>
            </li>
            </@shiro.user>
            <@menuItem menus 0/>
        </ul>
    </div>
</nav>
</#macro>