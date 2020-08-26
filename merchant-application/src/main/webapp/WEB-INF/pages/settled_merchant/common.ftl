<div class="header">基本信息</div>
<table class="table table-bordered table-hover">
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.name")}</td>
        <td class="col-value">
            ${settledMerchant.name!}
        </td>
        <td class="col-key">${_res.get("merchant.settled_merchant_type.name")}</td>
        <td class="col-value">
            ${(settledMerchant.settledMerchantType.name)!}
        </td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.status")}</td>
        <td>
            <#if settledMerchant.status??>
                        <span class="label <@statusLabel settledMerchant.status/>">
                            ${_res.get("merchant.settled_merchant." + settledMerchant.status?lower_case)}
                        </span>
            </#if>
        </td>
        <td class="col-key">${_res.get("merchant.settled_merchant.phone")}</td>
        <td class="col-value">${settledMerchant.phone!}</td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.contact_user")}</td>
        <td class="col-value">${settledMerchant.contact_user!}</td>
        <td class="col-key">${_res.get("merchant.settled_merchant.contact_phone")}</td>
        <td class="col-value">${settledMerchant.contact_phone!}</td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.contact_email")}</td>
        <td class="col-value">${settledMerchant.contact_email!}</td>
        <td class="col-key">${_res.get("merchant.settled_merchant.id_number")}</td>
        <td class="col-value">${settledMerchant.id_number!}</td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.business_license_number")}</td>
        <td class="col-value">${settledMerchant.business_license_number!}</td>
        <td class="col-key">${_res.get("merchant.settled_merchant.address")}</td>
        <td class="col-value">${settledMerchant.address!}</td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.userinfo")}</td>
        <td class="col-value" colspan="3">
            <span style="margin-right: 20px">
              <a href="/user/edit/${settledMerchant.userId}">
                ${settledMerchant.username!} - UID: ${settledMerchant.uid!}
               </a>
            </span>
            <#list settledMerchant.roles as role>
                <span class="label label-default">${role}</span>
            </#list>
        </td>
    </tr>
    <!--<tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.url")}</td>
        <td class="col-value" colspan="3">${wxHost!}/app?mid=${settledMerchant.id}</td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.qrcode")}</td>
        <td class="col-value" colspan="3"><div id="merchant-qrcode"></div></td>
    </tr>--!>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.description")}</td>
        <td class="col-value" colspan="3">${settledMerchant.description!}</td>
    </tr>
</table>

<div class="header">其他信息</div>
<table class="table table-bordered table-hover">
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.quality_ranking")}</td>
        <td class="col-value">${settledMerchant.quality_ranking!}</td>
        <td class="col-key">${_res.get("merchant.settled_merchant.attitude_ranking")}</td>
        <td class="col-value">${settledMerchant.attitude_ranking!}</td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.express_ranking")}</td>
        <td class="col-value">${settledMerchant.express_ranking!}</td>
    </tr>
</table>

<div class="header">图片信息</div>
<table class="table table-bordered table-hover">
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.logo")}</td>
        <td class="col-value">
            <div  class="cover-box">
                <img src="${settledMerchant.logo!}">
            </div>
        </td>
        <td class="col-key">${_res.get("merchant.settled_merchant.business_license_image")}</td>
        <td class="col-value">
            <div  class="cover-box">
                <img src="${settledMerchant.business_license_image!}">
            </div>
        </td>
    </tr>
    <tr>
        <td class="col-key">${_res.get("merchant.settled_merchant.id_front")}</td>
        <td class="col-value">
            <div  class="cover-box">
                <img src="${settledMerchant.id_front!}">
            </div>
        </td>
        <td class="col-key">${_res.get("merchant.settled_merchant.id_back")}</td>
        <td class="col-value">
            <div  class="cover-box">
                <img src="${settledMerchant.id_back!}" >
            </div>
        </td>
    </tr>
</table>
<div class="header">店铺介绍</div>
<div class="introduction">${settledMerchantIntroduction.introduction!}</div>

<div class="header">
    <span style="margin-right:10px;">处理记录</span>
    <a data-toggle="collapse" href="#collapseExample" aria-expanded="false" aria-controls="collapseExample" style="font-weight:normal;">
        查看
    </a>
</div>
<div class="collapse" id="collapseExample">
    <div style="margin-top:10px;">
        <#if settledMerchant.settledMerchantApproveLogs?size gt 0>
            <table class="table table-hover table-bordered table-striped">
                <tr>
                    <th style="width:10%">处理人</th>
                    <th style="width:15%">处理时间</th>
                    <th>处理意见</th>
                </tr>
                <#list settledMerchant.settledMerchantApproveLogs as settledMerchantApproveLog>
                    <tr>
                        <td>${settledMerchantApproveLog.administrator!}</td>
                        <td>${settledMerchantApproveLog.handled_date!}</td>
                        <td>${settledMerchantApproveLog.result!}</td>
                    </tr>
                </#list>
            </table>
            <#else>暂无处理流水
        </#if>
    </div>
</div>