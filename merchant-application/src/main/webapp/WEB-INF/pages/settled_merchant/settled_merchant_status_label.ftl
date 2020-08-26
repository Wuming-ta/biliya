<#macro statusLabel status>
    <#if status=="INIT">
      label-info
    <#elseif status=="APPROVING">
       label-primary
    <#elseif status=="APPROVED">
       label-success
    <#elseif status=="REJECTED">
      label-danger
    <#elseif status=="LOCKED">
      label-default
    </#if>
</#macro>