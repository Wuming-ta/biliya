<#macro list notifies>

        <div class="list-group" style="margin-top: 10px">
            <#list notifies.list as n>
            <a href="#" class="list-group-item" style="<#if n.is_read == 1>color: #aaa;</#if>">
                <img src="${n.avatar!}" height="60">
                ${_res.format("identity.user_join_notify.message", n.name, n.phone, n.join_time, productName!'')}
            </a>
            </#list>
        </div>
        <#if notifies.list?size == 0>
            <p>No Data</p>
        </#if>
</#macro>