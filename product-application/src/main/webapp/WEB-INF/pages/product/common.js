<script>
function calcPoint(sourceInput, targetId) {
<#if pointExchangeRate??>
    var rate = ${pointExchangeRate};
    var val = $(sourceInput).val() * rate;
    $("#"+targetId).html(val.toFixed(0));
</#if>
}

//建立一个可存取到该file的url
function getObjectURL(file) {
     var url = null ;
     if (window.createObjectURL!=undefined) { // basic
         url = window.createObjectURL(file) ;
     } else if (window.URL!=undefined) { // mozilla(firefox)
         url = window.URL.createObjectURL(file) ;
     } else if (window.webkitURL!=undefined) { // webkit or chrome
         url = window.webkitURL.createObjectURL(file) ;
     }
     return url ;
}
function previewVideo(element) {
    // Get a reference to the fileList
    var files = !!element.files ? element.files : [];
    // If no files were selected, or no FileReader support, return
    if (!files.length || !window.FileReader) return false;
     var objUrl = getObjectURL(files[0]);
     console.log("objUrl = "+objUrl) ;
     if (objUrl) {
         $(element).siblings("div").children("video").attr("src", objUrl);
         return true;
     }
     return false;
}

function videoDeleteClick(element) {
    $(element).siblings("input").remove();
    $(element).siblings("video").attr("src", null);
    clearInputFile($(element).parent("div").siblings("input")[0]);
}



function previewCover(element){
    // Get a reference to the fileList
    var files = !!element.files ? element.files : [];

    // If no files were selected, or no FileReader support, return
    if (!files.length || !window.FileReader) return false;

    // Only proceed if the selected file is an image
    if (/^image/.test( files[0].type)){
        // Create a new instance of the FileReader
        var reader = new FileReader();

        // Read the local file as a DataURL
        reader.readAsDataURL(files[0]);

        // When loaded, set image data as background of div
        reader.onloadend = function(){
            var $img = $(element).siblings("img");
            if ($img.length == 0) {
                $img = $(element).siblings("div").children("img");
            }
            var image = new Image();
            image.src = this.result;
            $img.attr("width", "100%");
            $img.attr("height", "100%");
            $img.attr("src", this.result);
        }

        return true;
    }
    return false;
}


function mouseEnterCover(element) {
    $(element).find("span").removeClass("hidden");
    $(element).find("img").css("opacity", 0.5);
}
function mouseLeaveCover(element) {
    $(element).find("span").addClass("hidden");
    $(element).find("img").css("opacity", 1);
}

function clearInputFile(f){
    if(f.value){
        try{
            f.value = ''; //for IE11, latest Chrome/Firefox/Opera...
        }catch(err){
        }
        if(f.value){ //for IE5 ~ IE10
            var form = document.createElement('form'), ref = f.nextSibling;
            form.appendChild(f);
            form.reset();
            ref.parentNode.insertBefore(f,ref);
        }
    }
}

/**
* 根据specCount的大小来切换skuTr标签的显示状态
*/
function changeSkuTrDisplay(specCount) {
    if(specCount <= 0) {
        $('#sku-tr').removeClass("hidden");
        $('#sku_id').removeClass("hidden");
    } else {
        $('#sku-tr').addClass("hidden");
        $('#sku_id').addClass("hidden");
    }
}


function removeProductSpec(element) {
    $(element).parents("tr:first").remove();

    var specCount = $('#spec-count').val();
    specCount--;
    $('#spec-count').val(specCount);

    <#if ((!virtual??) || (virtual?? && virtual == "0")) && wmsPluginEnabled>
    changeSkuTrDisplay(specCount);
    </#if>
}

var productSpecId = "-1";
function copyProductSpec(element, id) {
    var html = $("#product-spec-template").html()
        .replace(/#ID#/g, productSpecId--)
        .replace(/#NAME_VALUE#/g, $("#product-spec-name-" + id).val())
        .replace(/#COST_PRICE_VALUE#/g, $("#product-spec-cost-price-" + id).val())
        .replace(/#PRICE_VALUE#/g, $("#product-spec-price-" + id).val())
        .replace(/#POINT_VALUE#/g, $("#point-" + id).html())
        .replace(/#SUGGESTED_PRICE_VALUE#/g, $("#product-spec-suggested-price-" + id).val())
        .replace(/#WEIGHT_VALUE#/g, $("#product-spec-weight-" + id).val())
        .replace(/#STOCK_BALANCE_VALUE#/g, $("#product-spec-stock-balance-" + id).val());
    $(element).parents("tr:first").after(html);

    //添加规格的时候，更新spec-count标签的值（此标签用于记录页面现在有多少个规格，作用是如果有1个规格或以上，则产品不需要关联sku了，由规格来关联）
    var specCount = $('#spec-count').val();
    specCount++;
    $('#spec-count').val(specCount);

    <#if ((!virtual??) || (virtual?? && virtual == "0")) && wmsPluginEnabled>
    changeSkuTrDisplay(specCount);
    </#if>
}

// region selection
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
function regionSelectCallback(parameter, result) {
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
    console.log('regionSelectCallback:', nameList);
    $("#region").val(nameList);
    $("#region-static").text(nameList);
}

$(document).ready(function(){
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

    $("#region-select").click(function(){
        console.log('region select ');
        showSelectRegionDialog(null, regionSelectCallback);
    });


    $("#spec-create-btn").click(function() {
        var html = $("#product-spec-template").html()
            .replace(/#ID#/g, productSpecId--)
            .replace(/#NAME_VALUE#/g, "")
            .replace(/#COST_PRICE_VALUE#/g, "")
            .replace(/#PRICE_VALUE#/g, "")
            .replace(/#POINT_VALUE#/g, "")
            .replace(/#SUGGEST_PRICE_VALUE#/g, "")
            .replace(/#WEIGHT_VALUE#/g, "")
            .replace(/#STOCK_BALANCE_VALUE#/g, "1000")
            .replace(/#SKU_ID_VALUE#/g, "")
            .replace(/#SKU_CODE_VALUE#/g, "")
            .replace(/#SKU_NAME_VALUE#/g, "")
            .replace(/#BAR_CODE_VALUE#/g, "");

        $(this).parents("tr:first").before(html);

        //添加规格的时候，更新spec-count标签的值（此标签用于记录页面现在有多少个规格，作用是如果有1个规格或以上，则产品不需要关联sku了，由规格来关联）
        var specCount = $('#spec-count').val();
        specCount++;
        $('#spec-count').val(specCount);

        <#if ((!virtual??) || (virtual?? && virtual == "0")) && wmsPluginEnabled>
        changeSkuTrDisplay(specCount);
        </#if>
    });
});

</script>

<script type="text/html" id="product-spec-template">
    <tr>
        <td><input type="text" class="form-control required" id="product-spec-name-#ID#" name="productSpecification[#ID#].name" value="#NAME_VALUE#"></td>
        <td><input type="number" step="0.01" class="form-control required" id="product-spec-price-#ID#" name="productSpecification[#ID#].price" oninput="calcPoint(this,'point-#ID#');" value="#PRICE_VALUE#"></td>
        <td><p class="form-control-static" id="point-#ID#">#POINT_VALUE#</td>
        <td><input type="number" class="form-control required" id="product-spec-weight-#ID#" name="productSpecification[#ID#].weight" value="#WEIGHT_VALUE#"></td>
        <td><input type="number" step="0.01" class="form-control required" id="product-spec-cost-price-#ID#" name="productSpecification[#ID#].cost_price" value="#COST_PRICE_VALUE#"></td>
        <td><input type="number" step="0.01" class="form-control required" id="product-spec-suggested-price-#ID#" name="productSpecification[#ID#].suggested_price" value="#SUGGESTED_PRICE_VALUE#"></td>
        <#if (virtual?? && virtual == "1" ) || !wmsPluginEnabled>
        <td><input type="number" class="form-control required" id="product-spec-stock-balance-#ID#" name="productSpecification[#ID#].stock_balance" value="#STOCK_BALANCE_VALUE#"></td>
        </#if>
        <#if ((!virtual??) || (virtual?? && virtual == "0")) &&  wmsPluginEnabled>
            <td>
                <input type="hidden" class="skuid" id="product-spec-sku-id-#ID#" name="productSpecification[#ID#].sku_id" value="#SKU_ID_VALUE#">
                <input type="hidden" id="product-spec-sku-code-#ID#" name="productSpecification[#ID#].sku_code" value="#SKU_CODE_VALUE#">
                <input type="hidden" id="product-spec-sku-name-#ID#" name="productSpecification[#ID#].sku_name" value="#SKU_NAME_VALUE#">
                <input type="hidden" id="product-spec-bar-code-#ID#" name="productSpecification[#ID#].bar_code" value="#BAR_CODE_VALUE#">
                <div>
                    <a href="#" class="btn btn-info" data-toggle="modal" data-target="#skuSelectModal" data-spec-virtual-id="#ID#">关联SKU</a>
                </div>
                <div style="margin-top:3px;">
                    <span id="sku-select-result-#ID#"></span>
                </div>
            </td>
        </#if>
        <td>
            <a class="btn btn-danger btn-sm" onclick="removeProductSpec(this);">${_res.get("btn.delete")}</a>
            <a class="btn btn-primary btn-sm" onclick="copyProductSpec(this, #ID#);">${_res.get("btn.copy")}</a>
         </td>
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
