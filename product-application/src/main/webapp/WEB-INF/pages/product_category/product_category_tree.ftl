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
  <@shiro.hasPermission name="product.category.edit">
  ,href: "${base}/product_category/edit/${category.id}"
  ,tags: ['<a class="tag-white glyphicon glyphicon-pencil" href="${base}/product_category/edit/${category.id}" title="edit"></a>',
  '<a class="tag-white glyphicon glyphicon-trash" title="delete" href="${base}/product_category/delete/${category.id}" onclick="javascript:if (confirm(\'${_res.get("product_category.delete_confirm")}\')){return true;} return false;"></a>',
  '<a class="tag-white glyphicon glyphicon-plus" href="${base}/product_category/add?parent_id=${category.id}" title="add a sub-category"></a>'
  ]
  </@shiro.hasPermission>
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