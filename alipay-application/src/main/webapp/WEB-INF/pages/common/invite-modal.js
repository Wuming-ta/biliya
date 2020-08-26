<script>
    $(document).ready(function(){
       $('#modal-form').validate();
    });

    $('#myModal').on('show.bs.modal', function (event) {
      var button = $(event.relatedTarget);
      var modal = $(this);
      modal.find('#modal-form').attr('action', button.data('action'));
      modal.find('#kf_account').attr('value', button.data('kf_account'));
    });
</script>
