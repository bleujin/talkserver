<!DOCTYPE HTML>
<html>
	<head></head>
<body>
	<form method="POST">
	<textarea style='height:400px; width: 600px;' name="script">${self.property(script).stringValue()};</textarea>
	<br/ >
	<input type="submit" value="save">
	
	</form>
	
	<ul>
		${foreach self.children() child  }<li><a href='${child.fqn}'>${child.fqn}</a></li>${end}
	</ul>
	
</body>
</html>