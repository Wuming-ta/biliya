
<#macro form method>
<#include "_agentpanel.ftl" />
    <form id="agent_form" class="form-inline" role="form" action="${base}/agent/${method}" method="post">
        <#if (method=="save")>
            <div class="form-group">
                <input type="hidden" id="user_id" name="agent.user_id" class="form-control required">
                <span id="user_name" ></span>
                <button id="loadSuppliers" type="button" class="btn btn-info" data-toggle="modal" data-target="#agent_uid">检索用户</button>
            </div>
        </#if>

        <#if currentAgent??>
            <input type="hidden" id="currentUser_id" name="agent.user_id" class="form-control" value="${currentAgent.user_id!}">
            <input type="hidden" id="currentAgent_id" name="agent.id" class="form-control" value="${currentAgent.id}">
        </#if>

        <#if currentAgent??>
            <@agentPanel "省级代理" "provinces" 1 currentAgent.agentProvinces />
        <#else>
            <@agentPanel "省级代理" "provinces" 0 />
        </#if>

        <#if currentAgent??>
            <@agentPanel "市级代理" "cities" 1 currentAgent.agentCities />
        <#else>
            <@agentPanel "市级代理" "cities" 0 />
        </#if>

        <#if currentAgent??>
            <@agentPanel "区级代理" "districts" 1 currentAgent.agentDistricts />
        <#else>
            <@agentPanel "区级代理" "districts" 0 />
        </#if>

        <hr>
        <button id="submitBtn" type="submit" class="btn btn-primary">${_res.get("btn."+method)}</button>
        <a class="btn btn-default" href="javascript:history.back();">${_res.get("btn.back")}</a>
    </form>
</#macro>