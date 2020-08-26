<#macro trialApplicationStatus status>
    <#if status == "AUDITING" || status == "CREATED_PAY_PENDING">label-primary
    <#elseif status == "PAID_CONFIRM_PENDING">label-info
    <#elseif status == "DELIVER_PENDING" || status == "DELIVERING">label-warning
    <#elseif status == "DELIVERED">label-success
    <#else>label-default
    </#if>
</#macro>
