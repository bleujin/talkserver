<!DOCTYPE HTML>
<html>
	<head>
	<script src="/resource/jquery-1.10.2.min.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function () {
		    $('input').on('click', function () {
		        var Status = $(this).val();
		        
		        var paramData = {} ;
		        paramData['script'] = $("#script").val() ;
		        
			    jQuery.ajaxSettings.traditional = true;
		        $.ajax({
		            url: '/admin/script',
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
	    <p>Script Content
	    <br/ >example) session.root().children().toAdRows('name, age')</p>
        <textarea style='height:200px; width: 600px;' id="script"></textarea>
	</div>
	
	<div id="execute">
	    <input type='button' value="execute">
	    <div>
	        <p>Result</p>
	        <div style='height:50px; width: 600px;' id="result"></div>
	    </div>
	</div>
    </form>
    <!--
    session.tran(function(wsession){
   wsession.pathBy("/rooms/33/messages/2222")
   .refTo('sender', '/users/bleujin@i-on.net')
   .property('message', 'HelloWorld')
   .property('messageId', '2222')
   .property('requestId', '1234')
   .property('receivers', '')
   .property('options', 'event:onMessage') 
   .property('time', new Date().getTime()) 
   .property('clientScript', 'client.room().message(args.message)') ;
}) ;
    -->
</body>
</html>