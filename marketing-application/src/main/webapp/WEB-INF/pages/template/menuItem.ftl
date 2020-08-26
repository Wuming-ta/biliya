<#macro menuItem menuList level>
    <#assign defaultIcon = "<i class=\"fa fa-bars\" aria-hidden=\"true\"></i>" />
    <#list menuList as menu>
        <#if (menu.allowed) >
            <#if !menu.visible?? || (menu.visible?? && menu.visible == 1)>
                <#if (level == 0)><#local navLevel="nav-second-level"></#if>
                <#if (level >= 1)><#local navLevel="nav-third-level"></#if>
                <li <#if menu.selected>class="active"</#if>>
                    <#assign menuKey = menu.name/>
                    <#if (menu.subMenu?size > 0) >
                        <a <#if menu.selected>class="active-parent"</#if> href="#">${menu.icon!defaultIcon}${_res.get(menuKey)}<span class="fa arrow"></span></a>
                        <#local navIn="">
                        <#if menu.selected><#local navIn="in"></#if>
                        <ul class="nav ${navLevel} ${navIn}">
                          <@menuItem  menu.subMenu level + 1/>
                        </ul>
                    <#else>
                        <#assign activeMenu = "" />
                        <#if menu.selected>
                            <#assign activeMenu = "class=\"active-menu\"" />
                        </#if>
                        <a ${activeMenu!} href="${base}/${menu.url}">
                            <#if level == 0>${menu.icon!defaultIcon}</#if>
                            ${_res.get(menuKey)}
                        </a>
                    </#if>
                </li>
            </#if>
        </#if>
    </#list>
</#macro>
