<#macro trialEnabled enabled>
    <#if enabled?? && enabled?string == "1">label-primary
    <#else>label-default
    </#if>
</#macro>
