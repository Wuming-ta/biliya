<#include "materialNotification.ftl" />
<#macro subMenu menuList level>
    <#list menuList as menu>
        <#assign currentMenu = menu />
        <#if menu.selected>
            <#assign selected = true />
            <#break>
        </#if>
        <#list menu.subMenu as sub>
            <#if sub.selected>
                <#assign selected = true />
                <#break>
            </#if>
        </#list>
    </#list>

    <div class="nav-tabs-navigation sub-menu">
        <div class="nav-tabs-wrapper">
            <span class="nav-tabs-title">
            <span id="widget-configuration-btn" class="hidden">
                <a style="color: white; cursor:pointer;margin-right:10px;" title="自定义首页组件" onclick="showWidgetSettingsPannel();">
                    <i class="fa fa-cogs" aria-hidden="true"></i>
                </a>
            </span>
                ${productName!'KQD'}
            </span>
            <span class="nav-tabs-notification">
                <@notification />
                <span class="vertical-separator"></span>
                <#if currentUser?? && currentUser.avatar?? && currentUser.avatar != ''>
                    <#assign avatar = currentUser.avatar />
                <#else>
                    <#assign avatar>${base}/assets/img/find_user.png</#assign>
                </#if>
                <a href="${base}/profile" title="查看个人信息">
                    <img src="${avatar}" height="20" width="20" style="border-radius: 20px">
                </a>
                <a href="${base}/auth/logout">
                    <i class="fa fa-sign-out" aria-hidden="true"></i>${_res.get("header.logout")}
                </a>
            </span>
            <ul class="nav nav-tabs" style="border-radius: 0px;">
                <#if selected??>
                <#list currentMenu.subMenu as menu>
                    <#if menu.allowed && menu.visible?? && menu.visible==1>
                        <li <#if menu.selected>class="active"</#if> >
                            <a href="${base}/${menu.url}">
                                ${_res.get(menu.name)}
                             </a>
                        </li>
                    </#if>
                </#list>
                </#if>
            </ul>
        </div>
    </div>

</#macro>
