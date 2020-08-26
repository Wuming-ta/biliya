<script>
    $(document).ready(function(){
       $('#pwd-form').validate();

        $('#ok-btn').click(function(){
           var form = $('#form');
           var pwd = $('#pwd').val();
           var returnUrl = $('#return-url').val();
           form.append("<input type='hidden' name='pwd' value=" + pwd + " />");
           form.append("<input type='hidden' name='returnUrl' value=" + returnUrl + " />");
           form.submit();
       });
    });

    $('#myModal').on('show.bs.modal', function (event) {
      var button = $(event.relatedTarget);
      var modal = $(this);
      modal.find('#myModalLabel').html(button.data('title'));
      modal.find('#return-url').val(button.data('return-url'));
    });



</script>
