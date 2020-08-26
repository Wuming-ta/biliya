
<script type="text/html" id="product-select-result-template">
    <table class="table table-bordered">
        <tr>
            <th style="width:10%;">封面</th>
            <th style="width:18%;">产品名称</th>
            <th style="width:18%;">品牌</th>
            <th style="width:18%;">成本价</th>
            <th style="width:18%">市场价</th>
            <th style="width:18%;">零售价</th>
        </tr>
        <tr>
            <td><img height="50" src="#PRODUCT_COVER#" /></td>
            <td><a target="_blank" href="${base}/product/edit/#PRODUCT_ID#">#PRODUCT_NAME#</a></td>
            <td>#BRAND_NAME#</td>
            <td><span style="color:red;margin-left:15px;">¥#COST_PRICE#</span></td>
            <td><span style="color:red;margin-left:15px;">¥#SUGGESTED_PRICE#</span></td>
            <td><span style="color:red;margin-left:15px;">¥#PRICE#</span></td>
        </tr>
    </table>
</script>

<script>

function searchProduct() {
    var productName = $('#product-name').val();
    var categoryId = $('#category-id').val();
    getProducts(1, productName, categoryId);
};

function selectLink(productId, productName, costPrice, suggestedPrice, price, brandName, productCover) {
    var html = $('#product-select-result-template').html().replace(/#PRODUCT_ID#/g, productId)
        .replace(/#PRODUCT_NAME#/g, productName)
        .replace(/#COST_PRICE#/g, costPrice)
        .replace(/#SUGGESTED_PRICE#/g, suggestedPrice)
        .replace(/#PRICE#/g, price)
        .replace(/#BRAND_NAME#/g, brandName)
        .replace(/#PRODUCT_COVER#/g, productCover);
    $("#product-select-result").html(html);
    $("#product_id").val(productId);
    $('#myModal').modal('hide');
}

function getProducts(pageNumber, productName, categoryId) {
    var url = "${base}/product_select/listProducts?pageNumber=" + pageNumber + "&productName=" + productName + "&categoryId=" + categoryId  ;
    $.get(url, function(data) {
        $("#list-products").html(data);
    });
}

</script>