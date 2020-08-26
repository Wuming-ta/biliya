<script>

    function coverDeleteClick(element) {
        $(element).siblings("input").remove();
        var newInput = $(element).parent("div").siblings("input");
        var name = newInput.attr("name");
        $(element).siblings("img").attr("src", null);
        clearInputFile($(element).parent("div").siblings("input")[0]);
    }

    $(document).ready(function() {
        UE.getEditor('myEditor');

        var html = $('#product-select-result-template').html().replace(/#PRODUCT_ID#/g, '${trial.product.id!}')
        .replace(/#PRODUCT_NAME#/g, '${trial.product.name!}')
        .replace(/#COST_PRICE#/g, '${trial.product.cost_price!}')
        .replace(/#SUGGESTED_PRICE#/g, '${trial.product.suggested_price!}')
        .replace(/#PRICE#/g, '${trial.product.price!}')
        .replace(/#BRAND_NAME#/g, '${(trial.product.brand.name)!}')
        .replace(/#PRODUCT_COVER#/g, '${(trial.product.cover)!}');
        $("#product-select-result").html(html);

        $('#myModal').on('shown.bs.modal', function (e) {
            getProducts(1, '', '${categoryId!}');
        })

        $("#form").validate({
            submitHandler: function(form) {
                //check the cover
                var valid = false;
                $("input[type='file']").each(function(index, element) {
                    if ($(element)[0].value) {
                        valid = true;
                    }
                });
                $("input[name='cover-id']").each(function(index, element) {
                    if ($(element)[0].value) {
                        valid = true;
                    }
                });
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