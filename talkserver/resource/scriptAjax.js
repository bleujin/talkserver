var addProperty = function(){
    $('.property').append('<input type="text" class="propertyId" value><input type="text" class="propertyValue"><br />');
}

var execute = function(){
    $.ajax({
        type: "POST",
        url: "/aradon/jscript/ajax.string",
        data: "script=" + $("#script").val(),
        dataType: "html"
    }).done(function(msg){
            $("#result").html(msg);
        })
        .fail(function(msg){
            $("#result").html("Request failure.");
        });
}