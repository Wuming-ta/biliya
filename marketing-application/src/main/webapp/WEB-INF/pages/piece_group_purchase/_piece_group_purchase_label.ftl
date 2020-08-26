<#macro pieceGroupPurchaseStatus status>
    <#if status == "INIT">label-info
    <#elseif status == "ONSELL">label-primary
    <#else>label-default
    </#if>
</#macro>

<#macro pieceGroupPurchaseMasterStatus status>
    <#if status == "OPENING">label-primary
    <#elseif status == "DEAL">label-success
    <#else>label-default
    </#if>
</#macro>

<#macro pieceGroupPurchaseMemberStatus status>
    <#if status == "UNPAID">label-info
    <#elseif status == "PAID">label-primary
    <#else>label-default
    </#if>
</#macro>