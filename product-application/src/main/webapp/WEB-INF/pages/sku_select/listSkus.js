<script type="text/html" id="sku-select-result-template">
    <table class="table table-bordered">
        <tr>
            <th style="width:33.3%;">条形码</th>
            <th style="width:33.3%;">编号</th>
            <th style="width:33.3%;">名称</th>
        </tr>
        <tr>
            <td>#BAR_CODE#</td>
            <td>#SKU_CODE#</td>
            <td>#SKU_NAME#</td>
        </tr>
    </table>
</script>

<script>

/**
* specVirtualId是一个虚拟的id，原有项的虚拟id是真实的spec id（正数），按“+”号添加的项对应的spec id是负数。
*/
function searchSkus(specVirtualId) {
    var skuCode = $('#sku-code').val();
    var barCode = $('#bar-code').val();
    var skuName = $('#sku-name').val();
    var svi = specVirtualId ? specVirtualId : '';
    getSkus(1, skuCode, skuName, barCode, svi);
};

/**
* specVirtualId是一个虚拟的id，原有项的虚拟id是真实的spec id（正数），按“+”号添加的项对应的spec id是负数。
*/
function selectLink(skuId, skuCode, skuName, barCode, specVirtualId) {
    var html = $('#sku-select-result-template').html()
        .replace(/#SKU_CODE#/g, skuCode)
        .replace(/#SKU_NAME#/g, skuName)
        .replace(/#BAR_CODE#/g, barCode);

    var resultTagId = specVirtualId ? "sku-select-result-" + specVirtualId : "sku-select-result";  /*对于产品关联sku，使用sku-select-result，对于产品规格关联sku，使用sku-select-result-规格假id */
    $("#" + resultTagId).html(html);

    //有传specVirtualId的是产品规格项，没有传的是产品项
    var skuIdTagId = specVirtualId ? "product-spec-sku-id-" + specVirtualId : "sku_id";
    var skuCodeTagId = specVirtualId ? "product-spec-sku-code-" + specVirtualId : "sku_code";
    var skuNameTagId = specVirtualId ? "product-spec-sku-name-" + specVirtualId : "sku_name";
    var barCodeTagId = specVirtualId ? "product-spec-bar-code-" + specVirtualId : "bar_code";

    var stockBalanceTagId = specVirtualId ? "product-spec-stock-balance-" + specVirtualId : "stock_balance";

    $("#" + skuIdTagId).val(skuId);
    $("#" + skuCodeTagId).val(skuCode);
    $("#" + skuNameTagId).val(skuName);
    $('#' + barCodeTagId).val(barCode);

    //有关联sku id，则使用库存系统的库存，不使用这里的库存，故把库存输入框隐藏
    $('#' + stockBalanceTagId).css('display', 'none');

    $('#skuSelectModal').modal('hide');
}

function getSkus(pageNumber, skuCode, skuName, barCode, specVirtualId) {
    var url = "${base}/sku_select/listSkus?pageNumber=" + pageNumber + "&skuCode=" + skuCode + "&skuName=" + skuName + "&barCode=" + barCode;

    $.get(url, function(data) {
        var replacement = specVirtualId ? specVirtualId : "";
        var d = data.replace(/#SPEC_VIRTUAL_ID#/g, replacement);
        $("#list-skus").html(d);
    });
}

</script>