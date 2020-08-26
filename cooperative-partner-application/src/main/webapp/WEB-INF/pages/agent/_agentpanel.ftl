<#macro agentPanel title tag flag areas=0>
        <div class="panel panel-default panel-top">
            <div class="panel-heading">
                <span>${title}</span>
            </div>
            <div class="panel-body" id="${tag}Body">
                <#if (flag==1)>
                    <#list areas as area>
                        <div class="pcd-span" onmouseenter="deleteBtnVisible(this);" onmouseleave="deleteBtnHidden(this);">
                            <input type="hidden" value="${area.id}" name="pcdQualityId">
                            <input type="hidden" <#if area.agent_physical_settlement_percentage??>value="${area.id + '-' + area.agent_physical_settlement_percentage}"</#if>  name="agentPhysicalSettlementPercentage" />
                            <span class="pcd-box">${area.pcd_name} <#if area.agent_physical_settlement_percentage??>${area.agent_physical_settlement_percentage}</#if></span>
                            <span class="pcd-box-delete"><a href="#" onclick="removeFileLi(this);" class="glyphicon glyphicon-trash"></a></span>
                        </div>
                    </#list>
                </#if>
                <div class="pcd-div">
                  <span class="pcd-plus"><a class="glyphicon glyphicon-plus" href="#" data-toggle="modal" data-target="#pcd" data-whatever="${tag}"></a></span>
                </div>
            </div>
        </div>
</#macro>
