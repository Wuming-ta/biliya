<#macro select categories categoryId=0 level=1>
<#list categories as category>
<option value="${category.id}" <#if (categoryId??&&category.id==categoryId)>selected="selected"</#if> >|<#list 1..level as i>-</#list>${category.name}
<@select category.sub_categories categoryId level+4/>
</#list>
</#macro>