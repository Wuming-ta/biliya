  $(document).ready(function(){
    $("form").validate({
        submitHandler: function(form) {
            if ($(".purchase").size() > 0) {

                form.submit();
            }
        }
    });

    $("#reset-purchase-strategy").click(function() {
        $(".purchase").remove();
    });
  });


  var id = -1;
  function addPurchaseStrategy(element, strategyName) {
    if ($("." + strategyName.replace(/\./g, '-')).size() > 0) {
        return;
    }

    var container = $("#purchase-strategy-container");
    $.get("${base}/purchase_strategy/ajaxGetPurchaseStrategyTemplate/?name=" + strategyName, function(data) {
        var html = data.replace(/ID/g, id);
        id = id - 100;
        if($(".purchase").size() == 0) {
            html = html.replace(/VISIBLE/g, "hidden");
        }
        container.before(html);
    });
  }