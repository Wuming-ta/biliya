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

    function removeProductSpec(element) {
        $(element).parents("tr:first").remove();
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
                $.get("${base}/product/getProductCategoryProperties/"+id, function(data) {
                    $("#properties-div").html(data);
                });
            }
        });

        var productSpecId = "-1";
        $("#spec-create-btn").click(function() {
            var html = $("#product-spec-template").html().replace(/#ID#/g, productSpecId--);
            $(this).parents("tr:first").before(html);
        });

        var ue = UE.getEditor('myEditor');

        //resize the image
        $("img.cover").each(function(index, element) {
            if (element.width > element.height) {
                $(element).attr("width", "100%");
                $(element).attr("height", null);
            }
            else {
                $(element).attr("width", null);
                $(element).attr("height", "100%");
            }
        });
    });

</script>