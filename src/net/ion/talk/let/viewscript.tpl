<!DOCTYPE HTML>
<html>
	<head>
	<script src="/resource/jquery-1.10.2.min.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function () {
		    $('input').on('click', function () {
		        var Status = $(this).val();
		        
		        var paramData = {} ;
		        if($("#params").val().trim().length != 0){
			        var params = $("#params").val().split(",");
			
			        params.map(function(entry){
			            var entry_split = entry.split(":");
			            var key = entry_split[0];
			            var value = entry_split[1];
			            var values = (paramData[key.trim()] == undefined) ? [] : paramData[key.trim()] ;
			            values.push(value) ;
			            paramData[key.trim()] = values  ;
			        });
			        
			        $.each( paramData, function( key, value ) {
						paramData[key] = (value.length == 1) ? value[0] : value ;
					});
			    }
			    jQuery.ajaxSettings.traditional = true;
		        $.ajax({
		            url: '/execute/' + $("select[name=fnName]").val() + '.string',
		            method: 'POST', 
		            data: paramData,
		            dataType : 'html'
		        }).done(function(msg){
           			$("#result").html(msg);
        		}).fail(function(msg){
            		$("#result").html(msg);
        		});
		    });
		});

	</script>
	</head>
<body>
	<form method="POST">
    <div class="form">
	<br/ >
        <select name="fnName">
        	${foreach fnNames fnName }
        		<option value='${fnName}'>${fnName}</option>
    		${end}    	
        </select>
	    <p>Property
	    <br/ >example) name:alex, location:Oregon, money:10000</p>
        <textarea style='height:50px; width: 600px;' id="params"></textarea>
	</div>
	
	<div id="execute">
	    <input type='button' value="execute">
	    <div>
	        <p>Result</p>
	        <textarea style='height:50px; width: 600px;' id="result"></textarea>
	    </div>
	</div>
    </form>
</body>
</html>