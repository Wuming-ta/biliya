<#macro menu menuList level>
    <ul class="nav">
    <#list menuList as menu>
        <#if (menu.allowed) >
            <#if (menu.subMenu?size > 0) >
                <#assign subMenuSelected = "" />
                <#assign url = "" />
                <#list menu.subMenu as sub>
                    <#if sub.allowed && sub.selected && sub.visible?? && sub.visible == 1>
                        <#assign subMenuSelected = "class=\"active\"" />
                    </#if>
                    <#if url == "" && sub.allowed && sub.visible?? && sub.visible == 1>
                        <#assign url = sub.url />
                    </#if>
                </#list>

                <li ${subMenuSelected!}>
                <a href="${base}/${url!}">
                    ${menu.icon!}
                    <p>${_res.get(menu.name)}</p>
                 </a>
                 </li>
            <#else>
                <#if menu.selected>
                    <li class="active">
                        <a href="${base}/${menu.url}">
                            ${menu.icon!}
                            <p>${_res.get(menu.name)}</p>
                        </a>
                    </li>
                <#else>
                    <li>
                        <a href="${base}/${menu.url}">
                            ${menu.icon!}
                            <p>${_res.get(menu.name)}</p>
                        </a>
                    </li>
                </#if>
            </#if>
        </#if>
    </#list>
    </ul>
</#macro>
