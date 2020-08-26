<script>
    $(document).ready(function(){
        $("span.notify").each(function(index, element) {
            var url = $(element).data("badge-url");
            var timeout = $(element).data("timeout");
            if (url != null && url != "" && timeout != null && timeout != "") {
                getBadge(url, element);
                setInterval(function(){getBadge(url, element)}, timeout * 1000);
            }
        });

        function getBadge(url ,element) {
            $.get(url, function(data) {
                if (data != null && data != "" && data.indexOf("<") == -1) {
                    $(element).addClass("notification");
                    $(element).html(data);
                }
                else {
                    $(element).removeClass("notification");
                }
            });
        }
    });
</script>