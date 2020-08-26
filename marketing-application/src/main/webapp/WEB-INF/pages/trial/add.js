<script>
    $(document).ready(function() {
        UE.getEditor('myEditor');

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