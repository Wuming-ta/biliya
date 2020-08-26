<#macro menu>
<#include "menuItem.ftl"/>
<nav class="navbar-side" role="navigation">
    <div class="sidebar-collapse">
        <ul class="nav" id="main-menu">

            <li class="text-center">
                <a href="${base}/profile"><img src="${base}/assets/img/find_user.png" class="user-image img-responsive"/></a>
            </li>
            
            <@menuItem menus 0/>
        </ul>
    </div>
</nav>
</#macro>