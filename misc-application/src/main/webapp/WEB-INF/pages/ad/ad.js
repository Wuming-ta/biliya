function selectLink(url) {
    $("#target_url").val(url);
    $('#myModal').modal('hide');
}

$(document).ready(function(){
    $("#form").validate();

    $('a[aria-controls="product"]').on('shown.bs.tab', function (e) {
        getProduct(1, "");
    });

    $('#search-product').click(function() {
        var productName = $('#product-name').val();
        getProduct(1, productName);
    });


    <#if categoryLinkDefinition??>
    var tree = [<@tree categories/>];
    if (tree.length > 0) {
        $("#category").treeview({data: tree,
            nodeIcon: "glyphicon glyphicon-star-empty",
            enableLinks: true,
            levels: 3,
            showTags: true,
            onNodeSelected: function(event, data) {
                selectLink('${categoryLinkDefinition.url}' + data.id);
            }
        });
    }
    </#if>
});

function getProduct(pageNumber, productName) {
    var url = "${base}/ad/listProduct?&pageNumber=" + pageNumber + "&productName=" + productName;
    $.get(url, function(data) {
        $("#product").html(data);
    });
}
