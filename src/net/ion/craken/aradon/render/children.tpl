<!--children.tpl-->
<!doctype html>
<html>
<head>
</head>
<body>
	<h3>Self</h3>
	<a href='/admin/repository/html${parent.fqn}/'>${parent.fqn}</a>
	<br/>
<pre>${selfjson}</pre>	
    <div id="children">
    <h3>Children</h3>
	<ul>
	${foreach children child }
		    <li><a href='/admin/repository/html${child.fqn}/'>${child.fqn}</a></li>
	${end}</ul>
    </div>
</body>
</html>