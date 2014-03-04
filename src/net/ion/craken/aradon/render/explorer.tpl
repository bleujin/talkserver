<!--explorer.tpl-->
<!doctype html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="/admin/template/js/jsoneditor.css"/>
<style>
/*form { display: block; margin: 20px auto; background: #eee; border-radius: 10px; padding: 15px }*/
#progress { position:relative; width:400px; border: 1px solid #ddd; padding: 1px; border-radius: 3px; }
#bar { background-color: #B4F5B4; width:0%; height:20px; border-radius: 3px; }
#percent { position:absolute; display:inline-block; top:3px; left:48%; }
</style>
<script type="text/javascript" src="/admin/template/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="/admin/template/js/jquery.json-2.4.min.js"></script>
<script type="text/javascript" src="/admin/template/js/jsoneditor.js"></script>
<script type="text/javascript" src="/admin/template/js/ace.js" charset="UTF-8"></script>
<script type="text/javascript" src="/admin/template/js/mode-json.js" charset="UTF-8"></script>
<script type="text/javascript" src="/admin/template/js/theme-textmate.js" charset="UTF-8"></script>
<script type="text/javascript" src="/admin/template/js/theme-jsoneditor.js" charset="UTF-8"></script>
<script type="text/javascript" src="/admin/template/js/jsonlint.js" charset="UTF-8"></script>
<script src="http://malsup.github.com/jquery.form.js"></script>

<script type="text/javascript">
    var codeEditor,
            fileList = [],
            wsServer = null;

    function updateNode() {
        var json = codeEditor.get();

        if (json === null || json === '') {
            json = {};
        }

        $.post(document.location.href, {body: JSON.stringify(json)}, function (response) {
            document.location.reload();
        }).fail(function(e) {
        	alert('[' + e.status + ']' + e.statusText);
        });
    }

    function deleteNode() {
        $.ajax({
            type: 'DELETE',
            url: document.location.href,
            dataType: 'json',
            success: function(response) {
                document.location.reload();
            },
            error: function(response) {
                alert('error \n' + error);
            }
        });
    }

    $(document).ready(function () {
		var response = '${transformed}'.replace(/\\n/g, "\\\\n").replace(/\\t/g, "\\\\t");
        var json = $.parseJSON(response);

        codeEditor = new jsoneditor.JSONEditor(document.getElementById('codeEditor'), {mode: 'code'});
        codeEditor.set(json);
        codeEditor.focus();

        var uploadOption = {
            beforeSend: function()
            {
                return false;
                $("#progress").show();
                //clear everything
                $("#bar").width('0%');
                $("#message").html("");
                $("#percent").html("0%");
            },
            uploadProgress: function(event, position, total, percentComplete)
            {
                $("#bar").width(percentComplete+'%');
                $("#percent").html(percentComplete+'%');

            },
            success: function()
            {
                $("#bar").width('100%');
                $("#percent").html('100%');

            },
            complete: function(response)
            {
                $("#message").html("<font color='green'>"+response.responseText+"</font>");
                document.location.reload(true);
            },
            error: function()
            {
                $("#message").html("<font color='red'> ERROR: unable to upload files</font>");

            },
            beforeSubmit: function() {
                if($('input[name=uploadFile]').val() === '') {
                    return false;
                }
            }
        };

        $('#myForm').ajaxForm(uploadOption);
        var paths = document.location.pathname.split("/");
        var nodePath = '/' + paths.splice(4).join('/').replace('.html', '');
        $('input[name=path]').val(nodePath);
    });
</script>

</head>
<body>
<form id="myForm" action="/admin/upload" enctype="multipart/form-data" method="post">
    <input type="hidden" name="path" value="" />
    <input type="hidden" name="workspace" value="${workspace}"/>
    <div>
        <div id="codeEditor" style="width: 800px; height: 600px;"></div>
        <input type="button" name="update" value="Update" onclick="javascript:updateNode()"/> Or
        <input type="button" name="delete" value="delete" onclick="javascript:deleteNode()"/> Or
        <input type="file" name="uploadFile" value="upload"/>
        <input type="submit" value="Upload File"/>
        <br/><br/>
        <div id="progress">
            <div id="bar"></div>
            <div id="percent">0%</div>
        </div>
        <br/>
        <div id="message"></div>
        <br/><br/>
    </div>
    <h3>Parent</h3>
    <ul>
        <li><a href='/admin/repository/${workspace}/html${self.parent().fqn()}'>${self.parent().fqn()}</a></li>
    </ul>
    <h3>Children</h3>
    <div id="children">
	<ul>
	${foreach self.children() child }
		    <li><a href='/admin/repository/${workspace}/html${child.fqn}'>${child.fqn}</a></li>
	${end}</ul>
    </div>

</form>
</body>
</html>