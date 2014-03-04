<!--edit_value.tpl-->
<!doctype html>
<html>
<head>
	<script type="text/javascript" src="/admin/template/js/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="/admin/template/js/jquery.json-2.4.min.js"></script>

	<script type="text/javascript">
	    function updateNode() {
	    	var pValue = $('#property').val();
			var pName = getPropertyName();
	    	var obj = {};
	    	
	    	obj[pName] = pValue;
			var json = $.toJSON(obj);
				        
	        $.post(document.location.href, {body: json}, function (response) {
	            document.location.reload();
	        }).fail(function(e) {
	        	alert('[' + e.status + ']' + e.statusText);
	        });
	    }
	    
	    function getPropertyName() {
	    	var path = document.location.href;
	    	return path.substr(path.lastIndexOf('.')+1);
	    }
	    
	    $(document).ready(function() {
	    	$('#pName').html('Property : ' + getPropertyName());
	    	var json = $.parseJSON('${transformed}'.replace(/\\n/g, "\\\\n").replace(/\\t/g, "\\\\t"));
	    	var pName = getPropertyName();
	    	
	    	$('#property').val(json[pName]);
	    });
	</script>
</head>
<body>
<form id="myForm" method="post">
<div id="pName"></div>
<textarea id="property" name="" style="width:800px; height:600px;"></textarea>
</form>
<input type="button" value="update" onclick="javascript:updateNode()"/>
</body>
</html>