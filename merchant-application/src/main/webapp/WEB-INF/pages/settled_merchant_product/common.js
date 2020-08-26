<script>
function calcPoint(sourceInput, targetId) {
<#if pointExchangeRate??>
    var rate = ${pointExchangeRate};
    var val = $(sourceInput).val() * rate;
    $("#"+targetId).html(val.toFixed(0));
</#if>
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
        $('#sku-tr').css('display', '');
    } else {
        $('#sku-tr').css('display', 'none');
    }
}


function removeProductSpec(element) {
    $(element).parents("tr:first").remove();

    var specCount = $('#spec-count').val();
    specCount--;
    $('#spec-count').val(specCount);

    <#if wmsPluginEnabled>
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

    <#if wmsPluginEnabled>
    changeSkuTrDisplay(specCount);
    </#if>
}

$(document).ready(function(){
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

        <#if wmsPluginEnabled>
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
        <#if !wmsPluginEnabled>
        <td><input type="number" class="form-control required" id="product-spec-stock-balance-#ID#" name="productSpecification[#ID#].stock_balance" value="#STOCK_BALANCE_VALUE#"></td>
        </#if>
        <#if wmsPluginEnabled>
            <td>
                <input type="hidden" id="product-spec-sku-id-#ID#" name="productSpecification[#ID#].sku_id" value="#SKU_ID_VALUE#">
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
