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
        $("#product_form").validate({
            submitHandler: function(form) {
                    //check the cover
                    var valid = false;
                    $("input[type='file']").each(function(index, element) {
                        if ($(element)[0].value) {
                            valid = true;
                        }
                    });
                    if ($("input[name='cover-id']").length > 0) {
                        valid = true;
                    }
                    if (valid) {
                        form.submit();
                    }
                    else {
                        alert("请添加至少一个产品封面.");
                    }
                }
        });

        $("#category-id").change(function() {
            var id = $(this).val();
            if (id != null) {
                $.get("${base}/settled_merchant_product/getProductCategoryProperties/"+id, function(data) {
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
            getSkus(1, '', '', svi);
          })
        </#if>

        <#if (wmsPluginEnabled) && (product.productSpecifications?size gt 0)>
           $('#sku-tr').css('display', 'none');
        </#if>
    });

</script>