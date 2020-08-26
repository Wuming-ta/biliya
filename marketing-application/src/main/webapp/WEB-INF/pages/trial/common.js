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


