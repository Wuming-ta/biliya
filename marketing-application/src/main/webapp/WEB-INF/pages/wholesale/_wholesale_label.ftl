<#macro wholesaleStatus status>
    <#if status == "INIT">label-info
    <#elseif status == "ONSELL">label-primary
    <#else>label-default
    </#if>
</#macro>

<#macro wholesaleMemberStatus status>
    <#if status == "UNPAID">label-info
    <#elseif status == "PAID">label-primary
    <#else>label-default
    </#if>
</#macro>