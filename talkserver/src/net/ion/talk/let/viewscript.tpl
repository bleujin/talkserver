<!DOCTYPE HTML>
<html>
	<head>
	<script src="/static/jquery-1.10.2.min.js" type="text/javascript"></script>
	<script src="/static/scriptAjax.js" type="text/javascript"></script>
	</head>
<body>
    <div class="form">
	<form method="POST">
	<textarea style='height:400px; width: 600px;' name="script" id="script">${self.property(script).stringValue()};</textarea>
	<br/ >
	<div class="property">
	<button type="button" id="addProperty">Add property</button>
	</div>
	<input type="submit" value="save">
	</form>
	</div>
	<div id="execute">
	    <button onclick="execute();">execute</button>
	    <div id="result">
	    </div>
	</div>
	<div id="child>
	<ul>${foreach self.children() child }
		    <li><a href='${child.fqn}'>${child.fqn}</a></li>
	${end}</ul>
	</div>
</body>
</html>