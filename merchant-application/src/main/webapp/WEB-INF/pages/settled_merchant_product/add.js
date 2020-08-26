<script>

    function coverDeleteClick(element) {
        $(element).siblings("input").remove();
        $(element).siblings("img").attr("src", null);
        clearInputFile($(element).parent("div").siblings("input")[0]);
    }

    $(document).ready(function(){
        $("#category-id").change(function() {
            var id = $(this).val();
            if (id != null) {
                $.get("${base}/product/getProductCategoryProperties/"+id, function(data) {
                    $("#properties-div").html(data);
                });
            }
        });

        $("#category-id").trigger("change");

        $("#product_form").validate({
            submitHandler: function(form) {
                    //check the cover
                    var valid = false;
                    $("input[type='file']").each(function(index, element) {
                        if ($(element)[0].value) {
                            valid = true;
                        }
                    });
                    if (valid) {
                        form.submit();
                    }
                    else {
                        alert("请添加至少一个产品封面.");
                    }
                }
        });

        UE.getEditor('myEditor');

        $('#skuSelectModal').on('shown.bs.modal', function (event) {
            var button = $(event.relatedTarget);
            var specVirtualId = button.data('spec-virtual-id');

            var svi = specVirtualId ? specVirtualId : '';
            getSkus(1, '', '', svi);
        })
    });

</script>
