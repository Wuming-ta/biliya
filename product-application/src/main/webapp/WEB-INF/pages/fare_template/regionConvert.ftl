<#macro regionConv region>
<#list region?split("|") as r>
    ${r}
    <#if r_has_next>, </#if>
</#list>
</#macro>
