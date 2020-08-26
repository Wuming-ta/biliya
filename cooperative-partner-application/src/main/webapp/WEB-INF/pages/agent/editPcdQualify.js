
$(document).ready(function() {
    //load provinces
    $('#province-qualify-tbody').empty();
    $('#city-qualify-tbody').empty();
    $('#district-qualify-tbody').empty();
    $.get("${base}/agent/ajaxGetProvinces", function(data) {
        for (var p in data) {
            var html = $('#province-template').html()
                .replace(/#name#/g, data[p].name)
                .replace(/#physical_settlement_percentage#/g, data[p].physical_settlement_percentage)
                .replace(/#index#/g, data[p].id);
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
                                          .replace(/#physical_settlement_percentage#/g, data[p].physical_settlement_percentage)
                                          .replace(/#index#/g, data[p].id);
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
                                        .replace(/#physical_settlement_percentage#/g, data[p].physical_settlement_percentage)
                                        .replace(/#index#/g, data[p].id);
                                        $('#district-qualify-tbody').append(html);
                                }
                            });
                        }
                      });
                  });
              }
        });
    });


});