<script>
var cachedJson = null;
var selectRegionCallback = null;
var callbackParameter = null;

function showSelectRegionDialog(parameter, callback) {
    $('#select-region-modal').modal('show');
    selectRegionCallback = callback;
    callbackParameter = parameter;
}

function createRegionView() {
    var content = "";
    var template = $("#select-region-content-template").html();
    $.each(cachedJson, function(index, data) {
        if (index % 4 == 0) {
            if (index != 0) {
                content += "</div>";
            }
            content += "<div class=\"row\" style=\"margin-top: 10px;\">";
        }
        content += "<div class=\"col-md-3\">";

        var cityRegionTemplate = $("#select-city-region-content-template").html();
        var cityRegion = [];
        var parent = data.id;
        $.each(data.area_list, function(index, data) {
            cityRegion.push(cityRegionTemplate.replace(/#NAME#/g, data.name).replace(/#VALUE#/g, data.id).replace(/#PARENT#/g, parent));
        });

        content += template.replace(/#NAME#/g, data.name).replace(/#VALUE#/g, data.id).replace(/#CITY-REGION#/g, cityRegion.join(""));
        content += "</div>";
    });
    if (cachedJson.length > 0) {
        content += "</div>";//close the last row
    }

    $("#select-region-content").html(content);
}

function showCityRegion(element) {
    $(element).next().toggleClass("hidden");
}

function getParent(val) {
    var result = $.grep(cachedJson, function(data, index) {
        return data.id == val;
    });
    return result.length > 0 ? result[0] : {id: null, name: null};
}

$(document).ready(function() {
    $("#select-region-modal").on("shown.bs.modal", function(e) {
        var url = "${base}/fare_template/ajaxPcd";
        if (cachedJson == null) {
            $.ajax({
                url: url,
                success: function(result) {
                    cachedJson = result;
                    createRegionView();
                }
            });
        }
        else {
            createRegionView();
        }
    });


    $("#select-region-confirm-btn").click(function() {
        $("#select-region-modal").modal('hide');
        var result = [];
        $("input[name='carry-mode-region']:checked").each(function() {
            var parent = getParent($(this).data("parent"));
            result.push({id: $(this).val(), name: $(this).data("name"), parentId: parent.id, parentName: parent.name});
        });

        if (selectRegionCallback != null) {
            selectRegionCallback(callbackParameter, result);
        }
    });

});
</script>
<script>
  $(document).ready(function(){
    $("form").validate({
        submitHandler: function(form) {
            var checked = $("input[name='carry-mode']:checked").length >= 1;
            if (checked) {
                form.submit();
            }
            else {
                alert("请至少选择一种运送方式。");
            }
        }
    });

    //切换计价方式后, 取消选择运送方式。
    $("input[name='fareTemplate.valuation_model']").click(function(){
        $("input[name='carry-mode']").each(function(){
            if ($(this).prop("checked")) {
                $(this).trigger('click');
            }
        });
    });


    function shopAddrSelectCallback(parameter, result) {
        var nameList = "";
        $.each(result, function(index, data) {
            if (data.parentId != null) {
                nameList += data.parentName + "-";
            }
            nameList += data.name;
            if (index < result.length - 1) {
                nameList += ",";
            }
        });
        $("#shop_addr").val(nameList);
    }

    //宝贝地址选择
    $("#shop-addr-select").click(function(){
        showSelectRegionDialog(null, shopAddrSelectCallback);
    });

    //选择运送方式后, 显示出来
    $("input[name='carry-mode']").click(function() {
        var checked = $(this).prop("checked");
        var value = $(this).val();
        if (checked) {
            var html = $("#carry-mode-template").html().replace(/#CARRY_WAY#/g, value)
                .replace(/#KEY#/g, getValuationKey())
                .replace(/#FIRST_RESOURCE_NAME#/g, getValuationResourceFirst())
                .replace(/#SECOND_RESOURCE_NAME#/g, getValuationResourceSecond())
                .replace(/#ID#/g, GLOBAL_ID--)
                .replace(/#FIRST_VALUE#/g, 1)
                .replace(/#FIRST_AMOUNT#/g, 0)
                .replace(/#SECOND_VALUE#/g, 1)
                .replace(/#SECOND_AMOUNT#/g, 0)
                .replace(/#CARRY_MODE_ID#/g, "");
            $("#carry-mode-"+value).html(html);
            $("#carry-mode-"+value).removeClass("hidden");
        }
        else {
            $("#carry-mode-"+value).html("");
            $("#carry-mode-"+value).addClass("hidden");
        }
    });

    //选择条件包邮后, 显示出来
    $("input[name='fareTemplate.is_incl_postage_by_if']").change(function() {
        if ($(this).val()==1) {
            var html = $("#incl-postage-proviso-template").html();
            $("#incl-post-by-if").removeClass("hidden");
            $("#incl-post-by-if").html(html);
        }
        else {
            $("#incl-post-by-if").addClass("hidden");
        }
    });

  });

var GLOBAL_ID = 0;

//根据计价方式返回相应的key, 作为替换占位符用
function getValuationKey() {
    var valuationModel = $("input[name='fareTemplate.valuation_model']:checked").val();
    var key;
    switch (valuationModel) {
        case "0":
            key = "piece";
            break;
        case "1":
            key = "weight";
            break;
        case "2":
            key = "bulk";
            break;
    }
    return key;
}

function getValuationResourceFirst() {
    var firstPiece = "${_res.get("fare_template.first_piece")}";
    var firstWeight = "${_res.get("fare_template.first_weight")}";
    var firstBulk = "${_res.get("fare_template.first_bulk")}";
    var valuationModel = $("input[name='fareTemplate.valuation_model']:checked").val();
    switch (valuationModel) {
        case "0":
            return firstPiece;
            break;
        case "1":
            return firstWeight;
            break;
        case "2":
            return firstBulk;
            break;
    }
}

function getValuationResourceSecond() {
    var secondPiece = "${_res.get("fare_template.second_piece")}";
    var secondWeight = "${_res.get("fare_template.second_weight")}";
    var secondBulk = "${_res.get("fare_template.second_bulk")}";
    var valuationModel = $("input[name='fareTemplate.valuation_model']:checked").val();
    switch (valuationModel) {
        case "0":
            return secondPiece;
            break;
        case "1":
            return secondWeight;
            break;
        case "2":
            return secondBulk;
            break;
    }
}

//添加一行地区城市运费
function addRegion(element, carryWay) {
    var html = $("#carry-mode-region-template").html().replace(/#CARRY_WAY#/g, carryWay)
        .replace(/#KEY#/g, getValuationKey())
        .replace(/#FIRST_RESOURCE_NAME#/g, getValuationResourceFirst())
        .replace(/#SECOND_RESOURCE_NAME#/g, getValuationResourceSecond())
        .replace(/#ID#/g, GLOBAL_ID--)
        .replace(/#FIRST_VALUE#/g, 1)
        .replace(/#FIRST_AMOUNT#/g, 0)
        .replace(/#SECOND_VALUE#/g, 1)
        .replace(/#SECOND_AMOUNT#/g, 0)
        .replace(/#CARRY_MODE_ID#/g, "");
    $(element).parents("tr").before(html);
}

//删除一行地区城市运费
function removeCarryMode(element) {
    $(element).parents("tr").remove();
}

//添加一行地区条件包邮
function addInclPostageRegion(element) {
  var html = $("#incl-postage-proviso-region-template").html().replace(/#ID#/g, GLOBAL_ID--);
  $(element).parents("tr").before(html);
}

//删除一行地区条件包邮
function removeInclPostage(element) {
    $(element).parents("tr").remove();
}



//选择地区后填充运送地区框
function chooseRegionCallback(parameter, result) {
    var template = parameter.template;
    var templateCallback = parameter.templateCallback;
    var container = parameter.element;
    var theId = parameter.id;
    if (result.length == 0) {
        $(container).html("<span>未添加地区</span>");
        return;
    }

    var regionList = "";
    var nameList = "<small>";
    $.each(result, function(index, data) {
        if (data.parentId != null) {
            regionList += $.trim(data.parentName) + "-";
            nameList += $.trim(data.parentName) + "-";
        }
        regionList += $.trim(data.name);
        nameList += $.trim(data.name);
        if (index < result.length - 1) {
            regionList += "|";
            nameList += ",";
        }
    });
    nameList += "</small>";
    var html = templateCallback(template, regionList, nameList, theId);
    $(container).html(html);
}

//选择地区城市运送
function selectRegion(element, theId) {
    var template = "<input type='hidden' name='carryMode[#ID#].region' value='#VALUE#'>#NAME#";
    var parameter = {element: $(element).prev(), id: theId, template: template, templateCallback: evalRegionTemplate};
    showSelectRegionDialog(parameter, chooseRegionCallback);
}

function evalRegionTemplate(template, regionList, nameList, id) {
    return template.replace(/#VALUE#/g, regionList).replace(/#NAME#/g, nameList).replace(/#ID#/g, id);
}

//选择地区条件包邮
function selectRegionForInclPostage(element, theId) {
    var template = "<input type='hidden' name='inclPostageProviso[#ID#].region' value='#VALUE#'>#NAME#";
    var parameter = {element: $(element).prev(), id: theId, template: template, templateCallback: evalInclPostageRegionTemplate};
    showSelectRegionDialog(parameter, chooseRegionCallback);
}

function evalInclPostageRegionTemplate(template, regionList, nameList, id) {
    return template.replace(/#VALUE#/g, regionList).replace(/#NAME#/g, nameList).replace(/#ID#/g, id);
}


//更改包邮类型
function changeInclPostageType(element, theId) {
    <#list inclPostageTypes as type>
    $("#incl-postage-proviso-" + theId + "-type-" + ${type.value}).addClass("hidden");
    </#list>
    $("#incl-postage-proviso-" + theId + "-type-" + $(element).val()).removeClass("hidden");
}
</script>

<script type="text/html" id="carry-mode-template">
    <div>
        <span>默认运费: </span>
        <span>#FIRST_RESOURCE_NAME#</span>
        <span><input type="number" class="required" name="carryMode[#ID#].first_#KEY#" value="#FIRST_VALUE#"></span>
        <span>${_res.get("fare_template.first_amount")}</span>
        <span><input type="number" class="required" name="carryMode[#ID#].first_amount" value="#FIRST_AMOUNT#">元, </span>
        <span>#SECOND_RESOURCE_NAME#</span>
        <span><input type="number" class="required" name="carryMode[#ID#].second_#KEY#" value="#SECOND_VALUE#"></span>
        <span>${_res.get("fare_template.second_amount")}</span>
        <span><input type="number" class="required" name="carryMode[#ID#].second_amount" value="#SECOND_AMOUNT#">元</span>
        <input type="hidden" name="carryMode[#ID#].is_default" value="1">
        <input type="hidden" name="carryMode[#ID#].carry_way" value="#CARRY_WAY#">
        <input type="hidden" name="carryMode[#ID#].id" value="#CARRY_MODE_ID#">
    </div>
    <div style="margin: 5px;">
        <table class="table table-bordered">
            <tr>
                <th style="width: 40%">${_res.get("fare_template.region")}</th>
                <th style="width: 12.5%">#FIRST_RESOURCE_NAME#</th>
                <th style="width: 12.5%">${_res.get("fare_template.first_amount")}</th>
                <th style="width: 12.5%">#FIRST_RESOURCE_NAME#</th>
                <th style="width: 12.5%">${_res.get("fare_template.second_amount")}</th>
                <th style="width: 10%"></th>
            </tr>
            <!--carry-mode-region-template fill here-->
            <tr>
                <td colspan="9"><a id="carry-mode-region-add-#ID#" class="btn btn-link" onclick="addRegion(this, #CARRY_WAY#);">为指定地区城市设置运费</a></td>
            </tr>
        </table>
    </div>
</script>

<script type="text/html" id="carry-mode-region-template">
    <tr>
        <input type="hidden" name="carryMode[#ID#].id" value="#CARRY_MODE_ID#">
        <input type="hidden" name="carryMode[#ID#].carry_way" value="#CARRY_WAY#">
        <input type="hidden" name="carryMode[#ID#].is_default" value="0">
        <td>
            <span>未添加地区</span>
            <a id="carry-mode-region-select-#ID#" class="pull-right btn btn-link btn-sm" onclick="selectRegion(this, #ID#);">编辑</a>
        </td>
        <td><input type="number" class="required" name="carryMode[#ID#].first_#KEY#" value="#FIRST_VALUE#"></td>
        <td><input type="number" class="required" name="carryMode[#ID#].first_amount" value="#FIRST_AMOUNT#"></td>
        <td><input type="number" class="required" name="carryMode[#ID#].second_#KEY#" value="#SECOND_VALUE#"></td>
        <td><input type="number" class="required" name="carryMode[#ID#].second_amount" value="#SECOND_AMOUNT#"></td>
        <td><a class="btn btn-danger btn-sm" onclick="removeCarryMode(this);">删除</a></td>
    </tr>
</script>

<script type="text/html" id="incl-postage-proviso-template">
    <table class="table table-bordered">
        <tr>
            <th style="width: 40%">${_res.get("fare_template.incl_postage.region")}</th>
            <th style="width: 15%">${_res.get("fare_template.carry_mode")}</th>
            <th style="width: 35%">${_res.get("fare_template.incl_postage.type")}</th>
            <th style="width: 10%"></th>
        </tr>
        <!-- incl-postage-proviso-region-template fill here-->
        <tr>
            <td colspan="9"><a class="btn btn-link" onclick="addInclPostageRegion(this);">为指定地区城市设置条件包邮</a></td>
        </tr>
    </table>
</script>

<script type="text/html" id="incl-postage-proviso-region-template">
    <tr>
        <td>
            <span>未添加地区</span>
            <a class="pull-right btn btn-link btn-sm" onclick="selectRegionForInclPostage(this, #ID#);">编辑</a>
        </td>
        <td>
            <select class="form-control" name="inclPostageProviso[#ID#].carry_way">
                <#list carryWays as carryWay>
                    <option value="${carryWay.value}">${_res.get("fare_template.carry_way."+carryWay.value)}</option>
                </#list>
            </select>
        </td>
        <td>
            <select class="form-control" style="width: auto; display: inline-block;" name="inclPostageProviso[#ID#].type" onchange="changeInclPostageType(this, #ID#);">
                <#list inclPostageTypes as type>
                    <option value="${type.value}" <#if type_index==0>selected="selected"</#if> >${_res.get("fare_template.incl_postage.type."+type.value)}</option>
                </#list>
            </select>
            <#list inclPostageTypes as type>
                <input type="number" class="form-control required <#if type_index!=0>hidden</#if>" style="width: auto; display: inline-block;"
                       id="incl-postage-proviso-#ID#-type-${type.value}" name="inclPostageProviso[#ID#].${type?lower_case}">
            </#list>
        </td>
        <td><a class="btn btn-danger btn-sm" onclick="removeInclPostage(this);">删除</a></td>
    </tr>
</script>

<script type="text/html" id="select-region-content-template">
    <label>
        <input type="checkbox" name="carry-mode-region" value="#VALUE#" data-name="#NAME#">#NAME#
    </label>
    <a onclick="showCityRegion(this);"><i class="fa fa-chevron-circle-down" aria-hidden="true"></i></a>
    <div class="city-region hidden">#CITY-REGION#</div>
</script>

<script type="text/html" id="select-city-region-content-template">
    <label>
        <input type="checkbox" name="carry-mode-region" value="#VALUE#" data-name="#NAME#" data-parent="#PARENT#">#NAME#
    </label>
</script>
