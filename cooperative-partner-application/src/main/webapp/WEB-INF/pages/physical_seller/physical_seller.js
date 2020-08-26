$(document).ready(function() {
    //search pcd
    $('#p-select').change(function() {
          $('#c-select').empty();
          $('#d-select').empty();
          var id = $(this).val();
          if (id != null) {
              $.get("${base}/agent/ajaxGetCities?id="+id, function(data) {
                  var emptyHtml = $('#pcd-select-template').html().replace(/#value#/g, '').replace(/#name#/g, '');
                  $('#c-select').append(emptyHtml);

                  //不能用 var cp = ${cityQualify!0} ，否则生成的代码是 var cq =;  因为cityQualify还有一种可能:空字符串
                  var cq = <#if cityQualify?? && cityQualify!="">${cityQualify}<#else>0</#if>;
                  for (var p in data) {
                      var html = $('#pcd-select-template').html().replace(/#value#/g, data[p].id).replace(/#name#/g, data[p].name);
                      $('#c-select').append(html);

                      if(cq != 0 && cq == data[p].id){
                        var lastChild = $('#c-select option:last');
                        trigger(lastChild, $('#c-select'));
                      }
                  }
              });
          }
    });

    $('#c-select').change(function() {
          $('#d-select').empty();
          var id = $(this).val();
          if (id != null) {
              $.get("${base}/agent/ajaxGetDistricts?id="+id, function(data) {
                  var emptyHtml = $('#pcd-select-template').html().replace(/#value#/g, '').replace(/#name#/g, '');
                  $('#d-select').append(emptyHtml);

                   //不能用 var dq = ${districtQualify!0} ，否则生成的代码是 var dq =;  因为districtQualify还有一种可能:空字符串
                  var dq = <#if districtQualify?? && districtQualify!="">${districtQualify}<#else>0</#if>;
                  for (var p in data) {
                      var html = $('#pcd-select-template').html().replace(/#value#/g, data[p].id).replace(/#name#/g, data[p].name);
                      $('#d-select').append(html);

                      if(dq != 0 && dq == data[p].id) {
                        var lastChild = $('#d-select option:last');
                        trigger(lastChild, $('#d-select'));
                      }
                  }

              });
          }
    });

    $('#p-select').empty();
    $('#c-select').empty();
    $('#d-select').empty();
    $.get("${base}/agent/ajaxGetProvinces", function(data) {
      var emptyHtml = $('#pcd-select-template').html().replace(/#value#/g, '').replace(/#name#/g, '');
      $('#p-select').append(emptyHtml);

      //不能用 var pq = ${provinceQualify!0} ，否则生成的代码是 var pq =;  因为provinceQualify还有一种可能:空字符串
      var pq =<#if provinceQualify?? && provinceQualify!="">${provinceQualify}<#else>0</#if>
      for (var p in data) {
          var html = $('#pcd-select-template').html().replace(/#value#/g, data[p].id).replace(/#name#/g, data[p].name);
          $('#p-select').append(html);

          if(pq != 0 && pq == data[p].id){
            var lastChild = $('#p-select option:last');
            trigger(lastChild, $('#p-select'));
          }
      }
    });

});

    function trigger(firstOption, sel) {
        if (firstOption != null) {
            firstOption.attr("selected", "selected");
            sel.trigger('change');
        }
    }