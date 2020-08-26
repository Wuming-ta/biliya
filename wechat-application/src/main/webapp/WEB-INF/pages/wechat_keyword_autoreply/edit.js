<script>
$(document).ready(function() {
    $('#form').validate();

    var keywordId = "-1";
    $("#keyword-create-btn").click(function() {
        var html = $("#keyword-template").html();
        $(this).parents("tr:first").before(html);
    });
});

function removeKeyword(element) {
    $(element).parents("tr:first").remove();
}
</script>

<script type="text/html" id="keyword-template">
    <tr>
        <td><input class="form-control" name="keyword"></td>
        <td><a class="btn btn-danger btn-sm" onclick="removeKeyword(this);">${_res.get("btn.delete")}</a></td>
    </tr>
</script>