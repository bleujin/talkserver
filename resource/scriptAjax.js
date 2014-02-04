var execute = function(){


//    var data = JSON.parse("{}");
//    data.script = $("#script").val();
//
//    if($("#params").val().trim().length != 0){
//        var params = $("#params").val().split(",");
//
//        params.map(function(entry){
//            var entry_split = entry.split(":");
//            var key = entry_split[0];
//            var value = entry_split[1];
//            data[key.trim()] = value.trim();
//        });
//    }

    var data = "";

    data += "script=" + encodeURIComponent($("#script").val());


    if($("#params").val().trim().length != 0){
        var params = $("#params").val().split(",");

        params.map(function(entry){
            var entry_split = entry.split(":");
            var key = entry_split[0];
            var value = entry_split[1];
            data += "&" + key.trim() + "=" + value.trim();
        });
    }


    $.ajax({
        type: "POST",
        url: "/execute/ajax.json",
        data: data,
        dataType: "html"
    }).done(function(msg){
            $("#result").html(msg);
        })
        .fail(function(msg){
            $("#result").html(msg);
        });
}