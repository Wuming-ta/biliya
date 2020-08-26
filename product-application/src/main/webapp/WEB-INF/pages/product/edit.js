<script>
    var imageIndex = 1;
    function addImage(element) {
       var html = $('#image-template').html().replace(/#id#/g, imageIndex);
       imageIndex++;
       $(element).prev().append(html);
    }

    function removeImage(element) {
       $(element).parents('li.list-group-item').remove();
    }

    function removeImageThumb(element) {
       $(element).parent().remove();
    }

    function coverDeleteClick(element) {
        $(element).siblings("input").remove();
        var newInput = $(element).parent("div").siblings("input");
        var name = newInput.attr("name");
        $(element).siblings("img").attr("src", null);
        clearInputFile($(element).parent("div").siblings("input")[0]);
    }

    $(document).ready(function(){
        <#if product.productSpecifications??>
        $('#spec-count').val(${product.productSpecifications?size});
        <#else>
        $('#spec-count').val(0);
        </#if>

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

                    if (valid && (!coverSelected && $("input[name='cover-id']").length == 0)) {
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

        $("#category-id").change(function() {
            var id = $(this).val();
            if (id != null) {
                $.get("${base}/product/getProductCategoryProperties/"+id, function(data) {
                    $("#properties-div").html(data);
                });
            }
        });

        var ue = UE.getEditor('myEditor');

        //resize the image
        $("img.cover").each(function(index, element) {
            $(element).attr("width", "100%");
            $(element).attr("height", "100%");
        });

        <#if wmsPluginEnabled>
          $('#skuSelectModal').on('shown.bs.modal', function (event) {
            var button = $(event.relatedTarget);
            var specVirtualId = button.data('spec-virtual-id');

            var svi = specVirtualId ? specVirtualId : '';
            getSkus(1, '', '', '', svi);
          })
        </#if>

        <#if (wmsPluginEnabled) && (product.productSpecifications?size gt 0)>
           $('#sku-tr').addClass("hidden");
           $('#sku_id').addClass("hidden");
        </#if>
    });

</script>