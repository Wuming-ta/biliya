<#macro select types settledMerchantTypeId=0>
   <option value="">请选择</option>
   <#list types as type>
        <option value="${type.id!}" <#if (settledMerchantTypeId??&&type.id==settledMerchantTypeId)>selected="selected"</#if>>
            ${type.name!}
        </option>
   </#list>
</#macro>