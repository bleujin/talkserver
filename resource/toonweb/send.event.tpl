<!DOCTYPE html>
<html>
<head>
    <title>SendMessage EventSource ToonWeb</title>

</head>
<body>

<div id="outputDiv" style="overflow:scroll; width:580; height:100%"></div>


<script type="text/javascript">
    (function(){
        var ua = navigator.userAgent.toLowerCase();
		var check = function(r) {
		    return r.test(ua);
		};
		var isOpera = check(/opera/);
		var isIE = !isOpera && check(/msie/);
		
		if (isIE) {
			alert('not supported browser. use firefox') ;
			return ;
		}

        var es = new EventSource('/event/message/$senderId$/$topicId$');
        es.onopen = function() {
            logText('OPEN');
            console.log('open') ;
        };
        es.onmessage = function(event) {
            logText(event.data);
        };
        es.onerror = function() {
            logText('ERROR');
            console.log('open') ;
        };
        
		function createTableView(jsonText, theme, enableHeader) {
			// set optional theme parameter
		    if (theme === undefined) {
		        theme = 'mediumTable';  //default theme
		    }
		 
		    if (enableHeader === undefined) {
		        enableHeader = true; //default enable headers
		    }
	
		    // If the returned data is an object do nothing, else try to parse
		    // var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
		    if (jsonText.trim().indexOf('{') != 0) return jsonText ;
		    
		    var jsonObj = JSON.parse(jsonText) ;
		    var str = '<table class="' + theme + '" border=1 cellpadding=2 cellspacing=0 width="100%">';
		    str += '<tbody>';
	
	        var row = 0;
	        var keys = [] ;
			for (var index in jsonObj) {
				keys.push(index) ;
			}
			keys.sort().reverse() ;
			
	        for (var index in keys) {
	            str += '<tr>';
	 
	            if (enableHeader) {
	                str += '<th scope="row" width="80">' + keys[index] + '</th>';
	            }
	             
	            str += '<td style="word-break:break-all" >' + JSON.stringify(jsonObj[keys[index]]) + '</td>';
	            str += '</tr>';
	            row++;
	        }
		    str += '</tbody>'
		    str += '</table>';
		    return str;
		} ;
		
		function logText(str) {
            var log = document.getElementById("outputDiv");
	    	// var escaped = str.replace(/&/, "&amp;").replace(/</, "&lt;").replace(/>/, "&gt;").replace(/"/, "&quot;"); // "
	    	var escaped = str ;
	    	console.log(escaped) ;
	    	log.innerHTML = (createTableView(str,'',true)) + '<br/>'+  log.innerHTML ;
    		// log.innerHTML = "<div align='left' style=\"width:500; padding:7; background-color:efefef \">" + escaped + "</div><br />" + log.innerHTML;
        } ;

    })() ;
</script>

</body>
</html>