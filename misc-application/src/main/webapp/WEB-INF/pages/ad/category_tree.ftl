<#macro tree categories>
<#list categories as category>
{
  text: "${category.name}",
  cover: "${category.cover!}",
  promoted: "${category.promoted}",
  description: "${category.description!}",
  id: "${category.id}"
  <#if category.promoted==1>
  ,icon: "glyphicon glyphicon-star"
  </#if>
  <#if (category.sub_categories?size > 0) >
  ,nodes: [
    <@tree category.sub_categories/>
  ]
  </#if>
}
<#if (category_has_next)>
,
</#if>
</#list>
</#macro>