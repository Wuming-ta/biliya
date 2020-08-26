<#macro labelStatus status>
<#if status=="DRAFT">label-info
<#elseif status=="ONSELL">label-primary
<#else>label-default
</#if>
</#macro>