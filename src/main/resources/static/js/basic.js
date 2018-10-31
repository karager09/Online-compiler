function sendCode()
{
    var code = $("#code").val();

    $.ajax({
        url: "http://localhost:8080/api/compile/code",
        datatype: 'json',
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({
            code: code
        })


    }).then(function (data, status, jqxhr) {
        alert('Server responded: ' + data);
    });

}