<#macro labelStatus state>
<#if state=="PENDING_SETTLEMENT">label-info
<#elseif state=="SETTLED">
label-primary
<#else>
label-default
</#if>
</#macro>