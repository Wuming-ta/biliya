<script>

    function coverDeleteClick(element) {
        $(element).siblings("input").remove();
        $(element).siblings("img").attr("src", null);
        clearInputFile($(element).parent("div").siblings("input")[0]);
    }

    $(document).ready(function(){
        $('#spec-count').val(0);

        $("#category-id").change(function() {
            var id = $(this).val();
            if (id != null) {
                $.get("${base}/product/getProductCategoryProperties/"+id, function(data) {
                    $("#properties-div").html(data);
                });
            }
        });

        $("#category-id").trigger("change");


        $("#submitBtn").click(function() {
            $("#publish").val(false);
            $("#product_form").submit();
        });
        $("#publishBtn").click(function() {
            $("#publish").val(true);
            $("#product_form").submit();
        });

        $("#product_form").validate({
            submitHandler: function(form) {
                    var valid = true;
                    var message = "";
                    <#if wmsPluginEnabled>
                    var skuSelected = true;
                    $("input[type='hidden'][class='skuid'][class!='hidden']").each(function(index, element) {
                        if (!$(element)[0].value) {
                            skuSelected = false;
                        }
                    });
                    if (!skuSelected) {
                        valid = false;
                        message = "请选择SKU.";
                    }
                    </#if>

                    var coverSelected = false;
                    $("input[type='file']").each(function(index, element) {
                        if ($(element)[0].value) {
                            coverSelected = true;
                        }
                    });
                    if (valid && !coverSelected) {
                        valid = false;
                        message = "请添加至少一个产品封面.";
                    }

                    if (valid) {
                        form.submit();
                    }
                    else {
                        alert(message);
                    }
                }
        });

        UE.getEditor('myEditor');

        $('#skuSelectModal').on('shown.bs.modal', function (event) {
            var button = $(event.relatedTarget);
            var specVirtualId = button.data('spec-virtual-id');

            var svi = specVirtualId ? specVirtualId : '';
            getSkus(1, '', '', '', svi);
        })
    });

</script>
