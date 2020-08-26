<script>
    function removePricing(element) {
        $(element).parents("tr:first").remove();
    }

    $(document).ready(function() {
        UE.getEditor('myEditor');

        var pricingId = 1;
        $("#pricing-create-btn").click(function() {
            var html = $("#pricing-template").html().replace(/#ID#/g, pricingId++);
            $(this).parents("tr:first").before(html);
        });

        $('#myModal').on('shown.bs.modal', function (e) {
            getProducts(1, '', '');
        })

        $("#form").validate({
            submitHandler: function(form) {
                if($("input[name=payment_type]:checked").length == 0){
                    alert("请至少选择一种支付方式！");
                    return;
                }
                //check the cover
                var valid = false;
                $("input[type='file']").each(function(index, element) {
                    if ($(element)[0].value) {
                        valid = true;
                    }
                });
                if (valid) {
                     form.submit();
                }else{
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

    function coverDeleteClick(element) {
        $(element).siblings("input").remove();
        $(element).siblings("img").attr("src", null);
        clearInputFile($(element).parent("div").siblings("input")[0]);
    }
</script>

