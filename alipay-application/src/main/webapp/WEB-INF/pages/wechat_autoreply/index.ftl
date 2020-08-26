<#macro index text newses controllerKey id=-1>
    <div class="panel panel-primary">
                        <div class="panel-heading">文字</div>
                        <div class="panel-body">
                            <form action="${base}/${controllerKey!}/updateText/${text.id!}" method="post" role="form">
                                <input type="hidden" name="wechatAutoreply.id" value="${(text.id)!}" />
                                <textarea class="form-control <#if text.enabled == 0>filter<#else>green-border</#if> " style="width:100%;height:150px;margin-bottom:10px;" name="wechatAutoreply.content">${(text.content)!}</textarea>
                                <button type="submit" class="btn btn-primary">${_res.get("save")}</button>
                                <#if (text.enabled)?? && text.enabled == 1>
                                    <a class="btn btn-warning" style="color: #fff"  href="${base}/${controllerKey!}/disableText/${text.id!}">禁用</a>
                                <#else><a class="btn btn-success" href="${base}/${controllerKey!}/enableText/${text.id!}">启用</a>
                                </#if>
                            </form>
                        </div>
                    </div>
                    <div class="panel panel-primary">
                        <div class="panel-heading">图文</div>
                        <div class="panel-body">
                            <div>
                                <div><a href="#" class="btn btn-info" data-toggle="modal" data-target="#myModal" data-controller-key="${controllerKey!}" data-id="${id!}">选择图文消息</a></div>
                                <div class="row">
                                    <#list newses as news>
                                        <div class="col-md-6 ">
                                            <div class="box <#if news.enabled == 1>green-border</#if>"  >
                                                <div class="box-title <#if news.enabled == 0>filter</#if>" >
                                                    <a href="${news.url!}" target="_blank" style="color:black;" >
                                                        ${(news.title)!}
                                                    </a>
                                                    <span style="float:right;font-size:14px;">
                                                        ${(news.create_time?string('yyyy-MM-dd'))!}
                                                    </span>
                                                </div>
                                                <div class="box-digest <#if news.enabled == 0>filter</#if>">
                                                    <span class="text-muted">${(news.digest)!}</span>
                                                </div>
                                                <div class="box-btn">
                                                    <a class="btn btn-danger" onclick="return confirm('确定不使用此素材吗？');" href="${base}/${controllerKey!}/deleteNews/${(news.id!)}" >${_res.get("delete")}</a>
                                                    <#if (news.enabled)?? && news.enabled == 1>
                                                        <a class="btn btn-warning" style="color: #fff" href="${base}/${controllerKey!}/disableNews/${(news.id!)}">${_res.get("disable")}</a>
                                                        <#else><a class="btn btn-success" href="${base}/${controllerKey!}/enableNews/${(news.id!)}">${_res.get("enable")}</a>
                                                    </#if>
                                                </div>
                                            </div>
                                        </div>
                                    </#list>
                                </div>

                            </div>
                        </div>
                    </div>
</#macro>