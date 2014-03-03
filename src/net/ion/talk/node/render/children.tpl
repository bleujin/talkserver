<!--children.tpl-->
<!doctype html>
<html>
<head>
</head>
<body>
	<h3>Parent</h3>
	<a href='/node/html/${workspace}{$parent.fqn}'>${parent.fqn}</a>
	<br/>
    <div id="children">
    <h3>Children</h3>
	<ul>
	${foreach children child }
		    <li><a href='/node/html/${workspace}${child.fqn}'>${child.fqn}</a></li>
	${end}</ul>
    </div>
</body>
</html>