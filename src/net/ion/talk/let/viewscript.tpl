<!DOCTYPE HTML>
<html>
	<head>
	<script src="/resource/jquery-1.10.2.min.js" type="text/javascript"></script>
	<script src="/resource/scriptAjax.js" type="text/javascript"></script>
	</head>
<body>
    <div class="form">
	<form method="POST">
	<textarea style='height:400px; width: 600px;' name="script" id="script">${self.property(script).stringValue()}</textarea>
	<br/ >
        <input type="submit" value="save">
    </form>
	<div class="property">
	    <p>Property
	    <br/ >example) name:alex, location:Oregon, money:10000</p>
        <textarea style='height:50px; width: 600px;' id="params"></textarea>
	</div>
	</div>
	<div id="execute">
	    <button onclick="execute();">execute</button>
	    <div>
	        <p>Result</p>
	        <textarea style='height:50px; width: 600px;' id="result"></textarea>
	    </div>

	</div>
	<div id="child">
	<ul>
	<li><a href='${self.parent().fqn()}/'>${self.parent().fqn()}</a></li><br>
	${foreach self.children() child }
		    <li><a href='${child.fqn}'>${child.fqn}</a></li>
	${end}</ul>
	</div>
</body>
</html>