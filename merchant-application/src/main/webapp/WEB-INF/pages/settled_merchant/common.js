<script>
function calcPoint(sourceInput, targetId) {
<#if pointExchangeRate??>
    var rate = ${pointExchangeRate};
    var val = $(sourceInput).val() * rate;
    $("#"+targetId).html(val.toFixed(0));
</#if>
}

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
            if (image.width > image.height) {
                $img.attr("width", "100%");
                $img.attr("height", null);
            }
            else {
                $img.attr("height", "100%");
                $img.attr("width", null);
            }
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

function clearInputFile(f){
    if(f.value){
        try{
            f.value = ''; //for IE11, latest Chrome/Firefox/Opera...
        }catch(err){
        }
        if(f.value){ //for IE5 ~ IE10
            var form = document.createElement('form'), ref = f.nextSibling;
            form.appendChild(f);
            form.reset();
            ref.parentNode.insertBefore(f,ref);
        }
    }
}
</script>

<script type="text/html" id="product-spec-template">
    <tr>
        <td><input type="text" class="form-control required" id="product-spec-name-#ID#" name="productSpecification[#ID#].name"></td>
        <td><input type="number" step="0.01" class="form-control required" id="product-spec-price-#ID#" name="productSpecification[#ID#].price" oninput="calcPoint(this,'point-#ID#');"></td>
        <td><p class="form-control-static" id="point-#ID#"></td>
        <td><input type="number" class="form-control required" id="product-spec-weight-#ID#" name="productSpecification[#ID#].weight"></td>
        <td><input type="number" step="0.01" class="form-control required" id="product-spec-cost-price-#ID#" name="productSpecification[#ID#].cost_price"></td>
        <td><input type="number" step="0.01" class="form-control required" id="product-spec-suggested-price-#ID#" name="productSpecification[#ID#].suggested_price"></td>
        <td><input type="number" class="form-control required" id="product-spec-stock-balance-#ID#" name="productSpecification[#ID#].stock_balance" value="1000"></td>
        <td><a class="btn btn-danger btn-sm" onclick="removeProductSpec(this);">${_res.get("btn.delete")}</a></td>
    </tr>
</script>
