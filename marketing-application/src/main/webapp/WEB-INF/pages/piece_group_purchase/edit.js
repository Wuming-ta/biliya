<script>
    function removePricing(element) {
       $(element).parents("tr:first").remove();
    }

    function coverDeleteClick(element) {
        $(element).siblings("input").remove();
        var newInput = $(element).parent("div").siblings("input");
        var name = newInput.attr("name");
        $(element).siblings("img").attr("src", null);
        clearInputFile($(element).parent("div").siblings("input")[0]);
    }

    $(document).ready(function() {
        UE.getEditor('myEditor');

        var html = $('#product-select-result-template').html().replace(/#PRODUCT_ID#/g, '${pieceGroupPurchase.product.id!}')
        .replace(/#PRODUCT_NAME#/g, '${pieceGroupPurchase.product.name!}')
        .replace(/#COST_PRICE#/g, '${pieceGroupPurchase.product.cost_price!}')
        .replace(/#SUGGESTED_PRICE#/g, '${pieceGroupPurchase.product.suggested_price!}')
        .replace(/#PRICE#/g, '${pieceGroupPurchase.product.price!}')
        .replace(/#BRAND_NAME#/g, '${(pieceGroupPurchase.product.brand.name)!}')
        .replace(/#PRODUCT_COVER#/g, '${(pieceGroupPurchase.product.cover)!}');
        $("#product-select-result").html(html);

        var pricingId = "-1";
        $("#pricing-create-btn").click(function() {
            var html = $("#pricing-template").html().replace(/#ID#/g, pricingId--);
            $(this).parents("tr:first").before(html);
        });

        $('#myModal').on('shown.bs.modal', function (e) {
            getProducts(1, '', '');
        });

        $("#form").validate({
            submitHandler: function(form) {
                if($("input[name=payment_type]:checked").length == 0){
                    alert("请至少选择一种支付方式！");
                    return;
                }
                //check the cover
                var valid = false;
                if($("#cover").attr("src")){
                    valid = true;
                }
                if (valid) {
                    form.submit();
                }
                else {
                    alert("请添加活动封面.");
                }
            }
        });
    });

    function doSubmit() {
        if(!$('#product_id').val()) {
            alert('必须选择产品');
            return false;
        }
        return true;
    }
</script>