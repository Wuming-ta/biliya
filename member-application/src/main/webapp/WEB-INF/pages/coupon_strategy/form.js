<script>
    function trigger(firstOption, sel) {
        if (firstOption != null) {
            firstOption.attr("selected", "selected");
            sel.trigger('change');
        }
    }

    $(document).ready(function(){
        $('.datepicker').datepicker();
        $("#form").validate({
            rules: {
                couponTypeId: "required"
            }
        });
        $("#strategyType").change(function() {
            var val = $(this).children('option:selected').val();
            if (val === 'PRECISION_MARKETING') {
                $("#precisionMarketing").removeClass("hidden");
                $("#precisionMarketingPeriod").removeClass("hidden");
            }
            else {
                $("#precisionMarketing").addClass("hidden");
                $("#precisionMarketingPeriod").addClass("hidden");
                $("#couponStrategyTargetSome").addClass("hidden");
            }
        });
        $(":radio[name='couponStrategyTarget.type']").click(function(){
           var val = $(this).val();
           if (val === 'some') {
              $("#couponStrategyTargetSome").removeClass("hidden");
           }
           else {
              $("#couponStrategyTargetSome").addClass("hidden");
           }
        });
    });
</script>