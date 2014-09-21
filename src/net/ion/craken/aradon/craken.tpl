<!--craken.tpl-->
<!doctype html>
<html>
<head>
</head>
<body>

	<h4>Parent</h4>
	<a href='/admin/craken${self.parent().fqn()}'>${self.parent().fqn()}</a><br/>

	<h4>Self</h4>
	<a href='/admin/craken${self.fqn()}'>${self.fqn()}</a><br/>


	<h4>Properties</h4>
	<ul>
	${foreach self.toMap() entry }
		<li>${entry.getKey().idString()} : ${entry.getValue().asString()}</li>
	${end}
	</ul>

    <h4>Children</h4>
	<ul>
	${foreach self.children child }
		    <li><a href='/admin/craken${child.fqn}/'>${child.fqn}</a></li>
	${end}</ul>
    </div>
</body>
</html>