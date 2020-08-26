<script>
    $(document).ready(function(){
       $('#pwd-form').validate();
    });

    $('#myModal').on('show.bs.modal', function (event) {
      var button = $(event.relatedTarget);
      var modal = $(this);
      modal.find('#id').val(button.data('id'));
      modal.find('#pwd-form').attr('action', button.data('action'));
      modal.find('#return-url').val(button.data('return-url'));
    });
</script>
