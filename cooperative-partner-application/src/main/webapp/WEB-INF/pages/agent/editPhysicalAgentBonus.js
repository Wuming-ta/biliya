
var bonusId = -1;

$(document).ready(function() {
    $('#physical-agent-bonus-form').validate();

    //load provinces
    $('#province-qualify-tbody').empty();
    $('#city-qualify-tbody').empty();
    $('#district-qualify-tbody').empty();
    $.get("${base}/agent/ajaxGetProvinces", function(data) {
        for (var p in data) {
            var html = $('#province-template').html()
                .replace(/#name#/g, data[p].name)
                .replace(/#index#/g, data[p].id)
                .replace(/#pcd_id#/g, data[p].pcd_id);
            $('#province-qualify-tbody').append(html);
        }
     //search pcd
        $('.province-btn').click(function() {
              $('#city-qualify-tbody').empty();
              $('#district-qualify-tbody').empty();
              var id = $(this).val();
              if (id != null) {
                  $.get("${base}/agent/ajaxGetCities?id="+id, function(data) {
                      for (var p in data) {
                          var html = $('#city-template').html()
                                          .replace(/#name#/g, data[p].name)
                                          .replace(/#index#/g, data[p].id)
                                          .replace(/#pcd_id#/g, data[p].pcd_id);
                          $('#city-qualify-tbody').append(html);
                      }
                      $('.city-btn').click(function() {
                        $('#district-qualify-tbody').empty();
                        var id = $(this).val();
                        if (id != null) {
                            $.get("${base}/agent/ajaxGetDistricts?id="+id, function(data) {
                                for (var p in data) {
                                    var html = $('#district-template').html()
                                        .replace(/#name#/g, data[p].name)
                                        .replace(/#index#/g, data[p].id)
                                        .replace(/#pcd_id#/g, data[p].pcd_id);
                                        $('#district-qualify-tbody').append(html);
                                }
                            });
                        }
                      });
                  });
              }
        });
    });


    $('#physical-agent-bonus-add-btn').click(function() {
        var result = $('#physical-agent-bonus-template').html()
            .replace(/#bonus_id#/g, bonusId--)
            .replace(/#min_amount#/, '')
            .replace(/#max_amount#/, '')
            .replace(/#percentage#/, '');
        $('#physical-agent-bonus-add-btn').parents('tr:first').before(result);
    });

    $('#myModal').on('shown.bs.modal', function(e) {
        //设置form的action属性
        var btn = $(e.relatedTarget);
        var pcdId = btn.data('pcd-id');
        var bonusForm = $('#physical-agent-bonus-form');
        $(bonusForm).attr('action', '${base}/agent/updatePhysicalAgentBonus/' + pcdId);

        $('#physical-agent-bonus-form tr.physical-agent-bonus').remove();
        //把该地区已设置的奖金比例显示出来
        $.get('${base}/agent/ajaxGetPhysicalAgentBonus/' + pcdId, function(data) {
            for(var key in data) {
                var physicalAgentBonus = data[key];
                var result= $('#physical-agent-bonus-template').html()
                    .replace(/#min_amount#/g, physicalAgentBonus.min_amount)
                    .replace(/#max_amount#/g, physicalAgentBonus.max_amount)
                    .replace(/#percentage#/g, physicalAgentBonus.percentage)
                    .replace(/#bonus_id#/g, physicalAgentBonus.id);
                $('#physical-agent-bonus-add-btn').parents('tr:first').before(result);
            }
        });
    });

});

function removePhysicalAgentBonus(element) {
    $(element).parents("tr:first").remove();
}

function updatePhysicalAgentBonus() {
    var form = $('#physical-agent-bonus-form');
    $.ajax({
       type: "POST",
       url: form.attr('action'),
       data: form.serialize(),
       success: function() {
        alert('设置成功');
        $('#myModal').modal('hide');
       }
    });
}

