<script>
function getItem(pageNumber, controllerKey, id) {
    var url = "${base}/wechat_autoreply/listItem?&pageNumber=" + pageNumber;
    $.get(url, function(data) {
        var dataAfterReplace = data.replace(/#CONTROLLER_KEY#/g, controllerKey).replace(/#ID#/g, id);
        $('#item').html(dataAfterReplace);
    });
}

 function selectLink(controllerKey, id, title, digest, mediaId, url, thumbUrl, createTime) {
    var html = $('#item-select-result-template').html()
        .replace(/#CONTROLLER_KEY#/g, controllerKey)
        .replace(/#ID#/g, id)
        .replace(/#TITLE#/g, title).replace(/#DIGEST#/g, digest)
        .replace(/#CONTENT#/g, mediaId).replace(/#URL#/g, url)
        .replace(/#THUMB_URL#/g, thumbUrl)
        .replace(/#CREATE_TIME#/g, createTime);
    $('#item-select-result').html(html);
    $('#myModal').modal('hide');
    $('#news-form').submit();
}

$(document).ready(function() {
    $('#myModal').on('shown.bs.modal', function (e) {
        var btn = $(e.relatedTarget);
        getItem(1, btn.data('controller-key'), btn.data('id'));
    });
});
</script>


