function sendCode()
{
    var code = $("#code").val();

    $.ajax({
        url: "http://localhost:8080/api/compile/code",
        datatype: 'json',
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({
            code: code,
            language: "Cpp",
            runCompiledProgram : false
        })


    }).then(function (data, status, jqxhr) {

        $("#compileResponse").val('Status success, Server responded: ' + data);


    });

}