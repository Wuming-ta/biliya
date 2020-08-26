<script>
    $(document).ready(function(){
       $('#reason-form').validate();
    });

    $('#myModal').on('show.bs.modal', function (event) {
      var button = $(event.relatedTarget);
      var modal = $(this);
      modal.find('#id').val(button.data('id'));
      modal.find('#reason-form').attr('action', button.data('action'));
    });
</script>
