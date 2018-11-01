function sendCode()
{
    var code = $("#code").val();
    var input = $("#input").val();

    $.ajax({
        url: "http://localhost:8080/api/compile/code",
        datatype: 'json',
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({
            code: code,
            input: input,
            language: "Cpp",
            runCompiledProgram : false
        })


    }).then(function (data, status, jqxhr) {

        $("#compileResponse").val('Status success, Server response: \n' + data);


    });

}