<#macro statusLabel status>
    <#if status=="CREATED_PAY_PENDING">
      label-info
    <#elseif status=="PAID_CONFIRM_PENDING" || status=="CONFIRMED_DELIVER_PENDING">
      label-success
    <#elseif status=="DELIVERED_CONFIRM_PENDING" || status=="DELIVERING" || status=="DELIVERED_CONFIRM_PENDING">
      label-primary
    <#elseif status=="CANCELED_RETURN_PENDING" || status=="CANCELED_REFUND_PENDING">
      label-danger
    <#elseif status=="CLOSED_CONFIRMED">
      label-warning
    <#else>
      label-default
    </#if>
</#macro>