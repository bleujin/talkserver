<!DOCTYPE html>
<html>
<head>
    <title>EventSource ToonWeb</title>
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
</head>
<body>

<form id="frm01">
<table border=0 cellpadding=0 cellspacing="10">
	<tr><td><input type="checkbox" id="tohtml" checked="true"/>To HTML</td>
		<td><input type="checkbox" id="includereply" checked="true"/>Include Reply IN-STRING</td>
		<td><input type="checkbox" id="includeopenclose" checked="true"/>Include Open & Close</td>
		<td><input type="checkbox" id="includeheartbeat" />Include HeartBeat</td>
		<td><input type="checkbox" id="msgmatch" />Match in Msg <input type="text" id="matchstrings"></td>
		<td><input type="button" id="btnclear" value="Clear Screen"/></td>
		</tr>
</table>
</form>

<div id="outputDiv" style="overflow:scroll; width:580; height:380"></div>


<script type="text/javascript">
	String.prototype.startWith = function(str){
		return this.substr(0, str.length) == str ;
	} ;

    String.prototype.endsWith = function (s) {
		return this.length >= s.length && this.substr(this.length - s.length) == s;
	} ;
	String.prototype.isRequired = function (){
		return (this.trim() != '' && escape(this).replace(/%u3000/g, "").replace(/%20/g, "").length != 0 );
	};
	String.prototype.isNotRequired = function (){
		return ! this.isRequired();
	};
	String.prototype.isEmpty = function (){
		return this == '' ;
	};
	String.prototype.isBlank = function (){
		return this.trim() == '' ;
	};
	String.prototype.isNotBlank = function (){
		return ! this.isBlank() ;
	};
	String.prototype.isNotEmpty = function (){
		return ! this.isEmpty() ;
	};

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

        var es = new EventSource('/event/monitor/$userId$');
        es.onopen = function() {
            logText('OPEN');
        };
        es.onmessage = function(event) {
            logText(event.data);
        };
        es.onerror = function() {
            logText('ERROR');
        };
        
        \$("#btnclear").click(function(){
        	document.getElementById("outputDiv").innerHTML = '' ;
        }) ;
        
        function logText(str) {
            var log = document.getElementById("outputDiv");
	    	var escaped = str.replace(/&/, "&amp;").replace(/</, "&lt;").replace(/>/, "&gt;").replace(/"/, "&quot;"); // "
	    	// console.log(escaped) ;
	    	log.innerHTML = (createTableView(str,'',true)) + log.innerHTML ;
    		// log.innerHTML = "<div align='left' style=\"width:500; padding:7; background-color:efefef \">" + escaped + "</div><br />" + log.innerHTML;
        } ;
        
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
		    if (! \$("#tohtml").attr('checked')) return jsonText ; 
		    
		    
		    var jsonObj = JSON.parse(jsonText) ;
		    
			var matchstrings = \$("#matchstrings").val() ;
		    if (\$("#msgmatch").attr('checked') && matchstrings.isNotBlank()){
				var mcs = matchstrings.split(',')
				var found = false ;
				for(var i=0 ; i < mcs.length ; i++){
					if (jsonText.match(mcs[i])){
						found = true ;
					} 
				}
				if (! found) return '' ;
		    }
		    
		    if ( jsonObj.payload && jsonObj.payload != 'null' && jsonObj.payload != '' && jsonObj.payload.startWith('{') ) {
			    var payloadObj = JSON.parse(jsonObj.payload) ;
		    
			    if ((!\$("#includereply").attr('checked')) && jsonObj.action.endsWith('IN-STRING') &&  payloadObj.head.aradon == 'reply'){
				    return '';
			    }
			    if (!\$("#includeheartbeat").attr('checked') && payloadObj.head.command == 'ping') {
			    	return '' ;
			    }
		    }
		    
		    
		    
		    
		    if ((!\$("#includeopenclose").attr('checked')) && (jsonObj.action.endsWith('-OPEN') || jsonObj.action.endsWith('-CLOSE') )) return '';

		    
		    var str = '<table class="' + theme + '" border=1 cellpadding=2 cellspacing=0 width="100%">';
		    str += '<tbody>';
	
	        var row = 0;
	        var keys = [] ;
			for (var index in jsonObj) {
				keys.push(index) ;
			}
			keys.sort() ;
			
	        for (var index in keys) {
	            str += '<tr>';
	 
	            if (enableHeader) {
	                str += '<th scope="row" width="200">' + keys[index] + '</th>';
	            }
	             
	            str += '<td style="word-break:break-all" >' + jsonObj[keys[index]] + '</td>';
	            str += '</tr>';
	            row++;
	        }
		    str += '</tbody>'
		    str += '</table><br/><br/>';
		    return str;
		} ;
    })() ;
	
</script>

</body>
</html>