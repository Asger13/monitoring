<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>REST</title>
</head>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script type="text/javascript">
    var prefix = '/rest/myservice';

    var RestGet = function() {
        $.ajax({
            type: 'GET',
            url:  prefix + '/' + Date.now(),
            dataType: 'json',
            async: true,
            success: function(result) {
                alert('Время: ' + result.time
                        + ', сообщение: ' + result.message);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert(jqXHR.status + ' ' + jqXHR.responseText);
            }
        });
    }

</script>

<body>
    <!-- <button type="button" onclick="RestGet()">Метод GET</button> -->

</body>
</html>
