<#macro nav menus current>
<ol class="breadcrumb">
  <#assign keys = menus?keys>
  <#list keys as key>
    <li><a href="${base}${key}">${menus[key]}</a></li>
  </#list>
  <li class="active">${current}</li>
</ol>
</#macro>