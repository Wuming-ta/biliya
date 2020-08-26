function selectProduct(productId,productName) {
    $("#product_id").val(productId);
    $("#product_id_label").text(productName);
    $('#myModal').modal('hide');
}

function getProduct(pageNumber, productName) {
    var url = "${base}/coupon_type/listProduct?&pageNumber=" + pageNumber + "&productName=" + productName;
    $.get(url, function(data) {
        $("#product").html(data);
    });
}