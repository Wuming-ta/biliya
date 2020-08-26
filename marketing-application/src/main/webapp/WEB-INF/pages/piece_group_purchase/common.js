<script type="text/html" id="pricing-template">
    <tr>
        <td><input type="number" min="0" class="form-control required digits" id="pricing-participator-count-#ID#" name="pieceGroupPurchasePricing[#ID#].participator_count"></td>
        <td><input type="number" min="0" step="0.01" class="form-control required" id="pricing-price-#ID#" name="pieceGroupPurchasePricing[#ID#].price"></td>
        <td><a class="btn btn-danger btn-sm" onclick="removePricing(this);">${_res.get("btn.delete")}</a></td>
    </tr>
</script>

<script>
    function previewCover(element){
        // Get a reference to the fileList
        var files = !!element.files ? element.files : [];

        // If no files were selected, or no FileReader support, return
        if (!files.length || !window.FileReader) return false;

        // Only proceed if the selected file is an image
        if (/^image/.test( files[0].type)){
            // Create a new instance of the FileReader
            var reader = new FileReader();

            // Read the local file as a DataURL
            reader.readAsDataURL(files[0]);

            // When loaded, set image data as background of div
            reader.onloadend = function(){
                var $img = $(element).siblings("img");
                if ($img.length == 0) {
                    $img = $(element).siblings("div").children("img");
                }
                var image = new Image();
                image.src = this.result;
                $img.attr("width", "100%");
                $img.attr("height", "100%");
                $img.attr("src", this.result);
            }
            return true;
        }
        return false;
    }

    function mouseEnterCover(element) {
        $(element).find("span").removeClass("hidden");
        $(element).find("img").css("opacity", 0.5);
    }

    function mouseLeaveCover(element) {
        $(element).find("span").addClass("hidden");
        $(element).find("img").css("opacity", 1);
    }
</script>