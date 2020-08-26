$(document).ready(function() {

    $('#pcdSave').click(function(event) {
        var recipient = $("#recipient").val();
        if(recipient == "provinces"){
            var suppliers = $('#p-select option:selected').html();
            var id = $('#p-select option:selected').val();

            var result = false;
            $('input[name="pcdQualityId"]').each(function() {
                  var currentId = $(this).val();
                  if (id == currentId && result == false){
                    result = true;
                  }
            });
            if(!result){
               var span = $('#pcd-span-template').html().replace(/#pcd#/g, suppliers+" ").replace(/#id#/g, id).replace(/#agent_physical_settlement_percentage#/g, $('#agentPhysicalSettlementPercentage').val());
               $('#provincesBody .pcd-div').before(span);
            }

        }else if(recipient == "cities"){
            var suppliers = $('#c-select option:selected').html();
            var id = $('#c-select option:selected').val();

            var result = false;
            $('input[name="pcdQualityId"]').each(function() {
                  var currentId = $(this).val();
                  if (id == currentId && result == false){
                    result = true;
                  }
            });

            if(!result){
                var span = $('#pcd-span-template').html().replace(/#pcd#/g, suppliers+" ").replace(/#id#/g, id).replace(/#agent_physical_settlement_percentage#/g, $('#agentPhysicalSettlementPercentage').val());
                $('#citiesBody .pcd-div').before(span);
            }
        }else{
            var suppliers = $('#d-select option:selected').html();
            var id = $('#d-select option:selected').val();

            var result = false;
            $('input[name="pcdQualityId"]').each(function() {
                  var currentId = $(this).val();
                  if (id == currentId && result == false){
                    result = true;
                  }
            });

            if(!result){
                var span = $('#pcd-span-template').html().replace(/#pcd#/g, suppliers+" ").replace(/#id#/g, id).replace(/#agent_physical_settlement_percentage#/g, $('#agentPhysicalSettlementPercentage').val());
                $('#districtsBody .pcd-div').before(span);
            }
        }

        $('#pcd').modal('hide');
    });

    //search pcd
    $('#p-select').change(function() {
          $('#c-select').empty();
          $('#d-select').empty();
          var id = $(this).val();
          if (id != null) {
              $.get("${base}/agent/ajaxGetCities?id="+id, function(data) {
                  for (var p in data) {
                      var html = $('#pcd-select-template').html().replace(/#value#/g, data[p].id).replace(/#name#/g, data[p].name);
                      $('#c-select').append(html);
                  }

                  trigger($('#c-select option:first'), $('#c-select'));
              });
          }
    });

    $('#c-select').change(function() {
          $('#d-select').empty();
          var id = $(this).val();
          if (id != null) {
              $.get("${base}/agent/ajaxGetDistricts?id="+id, function(data) {
                  for (var p in data) {
                      var html = $('#pcd-select-template').html().replace(/#value#/g, data[p].id).replace(/#name#/g, data[p].name);
                      $('#d-select').append(html);
                  }

              });
          }
    });

    trigger($('#p-select option:first'), $('#p-select'));

    $('#pcd').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var recipient = button.data('whatever');
        var modal = $(this);
        $("#recipient").val(recipient);
        if(recipient == "provinces"){
            modal.find('#c-select').hide();
            modal.find('#d-select').hide();
        }else if(recipient == "cities"){
            modal.find('#c-select').show();
            modal.find('#d-select').hide();
        }else{
            modal.find('#c-select').show();
            modal.find('#d-select').show();
        }

        $('#p-select').empty();
        $('#c-select').empty();
        $('#d-select').empty();
        $.get("${base}/agent/ajaxGetProvinces", function(data) {
            for (var p in data) {
                var html = $('#pcd-select-template').html().replace(/#value#/g, data[p].id).replace(/#name#/g, data[p].name);
                $('#p-select').append(html);
            }
            trigger($('#p-select option:first'), $('#p-select'));
        });
    });
});

    function trigger(firstOption, sel) {
        if (firstOption != null) {
            firstOption.attr("selected", "selected");
            sel.trigger('change');
        }
    }

    function removeFileLi(element) {
        $(element).parents('.pcd-span').remove();
    }

    function deleteBtnVisible(element) {
        $(element).children('.pcd-box-delete').css("visibility","visible");
    }
    function deleteBtnHidden(element) {
        $(element).children('.pcd-box-delete').css("visibility","hidden");
    }

