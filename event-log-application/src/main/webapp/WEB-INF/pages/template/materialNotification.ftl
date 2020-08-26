<#macro notification>
    <#list notifications as n>
      <a <#if n.url??>href="${base}${n.url}"</#if> title="${n.title!}">
        <#if n.display_mode==0>${n.name!}</#if>
        <#if n.display_mode==1>${n.icon!}</#if>
        <#if n.badge_url??>
            <span class="notify" data-badge-url="${base}${n.badge_url!}" data-timeout="${n.timeout!}"></span>
        </#if>
      </a>
    </#list>
</#macro>
