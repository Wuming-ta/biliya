<#import "./default.ftl" as parent>
<#macro layout script="" css="" title="JFEAT" width=500>
<@parent.layout script css title width>
    <div class="col-xs-12 col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-4">
        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <#if logo??>
                        <div class="col-xs-6">
                            <a href="${base}/"><img src="${logo!}" height="40"></a>
                        </div>
                    </#if>
                    <div class="col-xs-6">
                        <span style="line-height: 40px">${productName!'Muaskin'}</span>
                    </div>
                </div>
            </div>
            <div class="panel-body">
                <#nested/>
            </div>
        </div>
    </div>
    </@parent.layout>
</#macro>