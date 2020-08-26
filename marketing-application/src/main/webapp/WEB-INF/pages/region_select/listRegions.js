<script>
    var GLOBAL_ID = -1;
    var cachedJson = null;
    var selectRegionCallback = null;
    var callbackParameter = null;

    $(document).ready(function() {
        $("#select-region-modal").on("shown.bs.modal", function(e) {
            var url = "${base}/region_select/ajaxPcd";
            if (cachedJson == null) {
                $.ajax({
                    url: url,
                    success: function(result) {
                        cachedJson = result;
                        createRegionView();
                    }
                });
            } else {
                createRegionView();
            }
        });

        $("#select-region-confirm-btn").click(function() {
            $("#select-region-modal").modal('hide');
            var result = [];
            $("input[name='region']:checked").each(function() {
                var parent = getParent($(this).data("parent"));
                result.push({id: $(this).val(), name: $(this).data("name"), parentId: parent.id, parentName: parent.name});
            });

            if (selectRegionCallback != null) {
                selectRegionCallback(callbackParameter, result);
            }
        });

    });

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

    //添加一行地区
    function addRegion(element, modelName) {
        var html = $("#region-template").html().replace(/#ID#/g, GLOBAL_ID--).replace(/#MODEL_NAME#/g, modelName);
        $(element).parents("tr:first").before(html);
    }

    //删除一行地区
    function removeRegion(element) {
        $(element).parents("tr:first").remove();
    }

    //选择地区城市
    function selectRegion(element, theId, modelName) {
        var template = '<input type="hidden" name="#MODEL_NAME#[#ID#].region" value="#VALUE#">#NAME#'.replace(/#MODEL_NAME#/g, modelName);
        var parameter = {element: $(element).prev(), id: theId, template: template, templateCallback: evalRegionTemplate};
        showSelectRegionDialog(parameter, chooseRegionCallback);
    }

    function evalRegionTemplate(template, regionList, nameList, id) {
        return template.replace(/#VALUE#/g, regionList).replace(/#NAME#/g, nameList).replace(/#ID#/g, id);
    }

    //选择地区后填充地区框
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

    function showSelectRegionDialog(parameter, callback) {
        $('#select-region-modal').modal('show');
        selectRegionCallback = callback;
        callbackParameter = parameter;
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

</script>

<script type="text/html" id="region-template">
    <tr>
        <input type="hidden" name="#MODEL_NAME#[#ID#].is_default" value="0">
        <td>
            <span>未添加地区</span>
            <a id="region-select-#ID#" class="pull-right btn btn-link btn-sm" onclick="selectRegion(this, #ID#, '#MODEL_NAME#');">编辑</a>
        </td>
        <td><input type="number" class="required" name="#MODEL_NAME#[#ID#].price"></td>
        <td><input type="number" name="#MODEL_NAME#[#ID#].suggested_retail_price"></td>
        <td><input type="number" name="#MODEL_NAME#[#ID#].suggested_wholesale_price"></td>
        <td>
            <span>
                <label class="radio-inline">
                    <input type="radio" name="#MODEL_NAME#[#ID#].enabled" value="1" checked="checked" />启用
                </label>
                <label class="radio-inline">
                    <input type="radio" name="#MODEL_NAME#[#ID#].enabled" value="0" />禁用
                </label>
            </span>
        </td>
        <td><a class="btn btn-danger btn-sm" onclick="removeRegion(this);">删除</a></td>
    </tr>
</script>

<script type="text/html" id="select-region-content-template">
    <label>
        <input type="checkbox" name="region" value="#VALUE#" data-name="#NAME#">#NAME#
    </label>
    <a onclick="showCityRegion(this);"><i class="fa fa-chevron-circle-down" aria-hidden="true"></i></a>
    <div class="city-region hidden">#CITY-REGION#</div>
</script>

<script type="text/html" id="select-city-region-content-template">
    <label>
        <input type="checkbox" name="region" value="#VALUE#" data-name="#NAME#" data-parent="#PARENT#">#NAME#
    </label>
</script>