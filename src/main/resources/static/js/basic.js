var editor = null;
var taskId = -1;
var lang = "cpp";
var hintIter = 0;

CodeMirror.commands.autocomplete = function (cm) {
    cm.showHint({hint: CodeMirror.hint.anyword});
}

$(document).ready(function () {
    createEditor();
    getListOfExercises();
});

function createEditor() {
    var code = $(".codemirror-textarea")[0];
    editor = CodeMirror.fromTextArea(code, {
        lineNumbers: true,
        lineWrapping: true,
        addModeClass: true,
        styleActiveLine: true,
        matchBrackets: true,
        autoCloseBrackets: true,
        showTrailingSpace: true,
        mode: "clike",
        dragDrop: false,
        fullScreen: false,
        highlightSelectionMatches: {showToken: /\w/, annotateScrollbar: true},
        extraKeys: {
            "Esc": function (cm) {
                if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
            },
            "Ctrl-L": function (cm) {
                cm.foldCode(cm.getCursor());
            },
            "Alt-F": "findPersistent",
            "Ctrl-Space": "autocomplete"
        },
        foldGutter: true,
        gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
    });

    setLanguage("cpp");
    loadTheme();

    setSizingOfEditorOnChange();
}

function setSizingOfEditorOnChange() {
    var height = $(window).height() - 245;
    var width = $(window).width() - 255; //225
    editor.setSize(width, height);
    $("#divWithTextAreas")[0].style.width = width + "px";
    $("#input")[0].style.width = width / 3 +"px";
    $("#compileResponse")[0].style.width = width / 3+"px";
}

function makeReadOnly(lines) {
    for (var i = 0; i < lines.length; i++) {
        var number = parseInt(lines[i]);
        editor.markText({line: number, ch: 0}, {line: number, ch: 200}, {
            readOnly: true,
            className: "styled-background"
        });
    }
}


function setLanguage(selectedLanguage) {
    language = selectedLanguage.toLowerCase();
    lang = language;
    $("#currentLanguage")[0].textContent = lang.toUpperCase();
    if (language == "java") {
        editor.setOption("mode", "text/x-java");
    } else if (language == "cpp") {
        editor.setOption("mode", "text/x-c++src");
    } else if (language == "tcl") {
        editor.setOption("mode", "text/x-tcl");
    }
}

function uploadFile() {
    var file = document.getElementById("uploadedFile");

    if ('files' in file) {
        if (file.files.length != 0) {
            var file = file.files[0];

            if ('type' in file) {
                setLanguage(file.type);
            }

            var reader = new FileReader();
            reader.onload = function (e) {
                var text = reader.result;
                editor.setValue(text);
            }
            reader.readAsText(file);
        }
    }
}

function saveCodeToFile() {
    var text = editor.getValue();
    var filename = "code.txt";

    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
}

function saveOutputToFile() {
    var text = document.getElementById('compileResponse').value;
    var filename = "output.txt";

    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
}

function selectTheme(theme) {
    editor.setOption("theme", theme);
    location.hash = "#" + theme;
}

function loadTheme() {
    var choice = (location.hash && location.hash.slice(1)) || (document.location.search && decodeURIComponent(document.location.search.slice(1)));

    if (choice) {
        editor.setOption("theme", choice);
    }
    CodeMirror.on(window, "hashchange", function () {
        var theme = location.hash.slice(1);
        if (theme) {
            selectTheme(theme);
        }
    });
}

function fullscreen() {
    if (editor.getOption("fullScreen")) {
        editor.setOption("fullScreen", false);
    } else {
        editor.setOption("fullScreen", true);
    }
}


function parseJsonWithExercisesList(returnMessage) {
    var list = $("#exercisesList");
    var obj = JSON.parse(returnMessage);

    for (var j = 0; j < obj.length; j++) {
        var language = obj[j].language;

        var hrefName = language + "submenu";
        var aElem = $('<a href="#'+ hrefName +'" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle">' + language.toUpperCase() + '</a>');
        var ulElem = $('<ul class="collapse list-unstyled" id="' + hrefName + '">');

        var elements = obj[j].elements;
        for (var i = 0; i < elements.length; i++) {
            var name = elements[i].name;
            var id = elements[i].id;

            var liElem = $('<li></lo><a href="#" onclick="getExercise(' + id + ')" id="' + id + '">' + name + '</a></li>');
            ulElem.append(liElem);

        }

        list.append(aElem);
        list.append(ulElem);
    }
}

function parseJsonWithExercise(exercise) {
    var obj = JSON.parse(exercise);
    var name = obj.name;
    var id = obj.id;
    var code = obj.code;
    var inactive = obj.inactive;
    var contents = obj.contents;
    var hints = obj.hints;
    var language = obj.language;

    setLanguage(language);
    editor.setValue(code);
    makeReadOnly(inactive);

    taskId = parseInt(id);

    var compilerModeDescription = $("#compilerModeDescription")[0];
    compilerModeDescription.textContent = "Current editor mode: Exercise - " + name.toUpperCase() + " - " + contents;

    hintIter = 0;
    $("#showHintButton").unbind('click');
    $("#showHintButton").on("click", function () {
        showHints(hints);
    });

    $("#labelWithResponse")[0].style.color = "black";
    $("#compileResponse")[0].style.backgroundColor = "white";
}

function clearEditor() {
    editor.setValue("");

    var compilerModeDescription = $("#compilerModeDescription")[0];
    compilerModeDescription.textContent = "Current editor mode: Editor";

    taskId = -1;

    $("#showHintButton").unbind('click');

    $("#labelWithResponse")[0].style.color = "black";
    $("#compileResponse")[0].style.backgroundColor = "white";
}

function showHints(hints) {
    if (hints.length == 0){
        swal({
            title: "HINT!",
            text: "Unfortunately there is no hints",
            imageUrl: 'images/hint.png'
        });
    } else {
        var hint = hints[hintIter];
        swal({
            title: "HINT!" + "(" + (hintIter+1) + "/" + hints.length + ")",
            text: hint,
            imageUrl: 'images/hint.png'
        });
        hintIter += 1;
        if (hintIter == hints.length){
            hintIter = 0;
        }
    }
}


function getExercise(id) {
    $.ajax({
        url: "http://localhost:8080/api/exercise/" + id,
        datatype: 'json',
        type: "get"
    }).then(function (data, status, jqxhr) {
        parseJsonWithExercise(data);
    });
}

function getListOfExercises() {
    $.ajax({
        url: "http://localhost:8080/api/exercise/list",
        datatype: 'json',
        type: "get"
    }).then(function (data, status, jqxhr) {
        parseJsonWithExercisesList(data);
    });
}

function sendCode() {
    var code = editor.getValue();
    var input = $("#input").val();
    var language = lang;
    $.ajax({
        beforeSend: function(){
          /*  $("#loadingImage")[0].style.visibility = "visible";*/
        },
        url: "http://localhost:8080/api/compile/code",
        datatype: 'json',
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({
            code: code,
            input: input,
            language: language,
            runCompiledProgram: false,
            taskId: taskId
        }),
        complete: function(){
          /*  $("#loadingImage")[0].style.visibility = "hidden";*/
        }
    }).then(function (data, status, jqxhr) {
        var obj = JSON.parse(data);
        parseOutput(obj.compilationSucceeded, obj.lineOfError, obj.outputOk, obj.response, obj.error);
    });
}

function parseOutput(compilationSucceeded, lineOfError, outputOk, response, error) {
    var text = "";
    if (compilationSucceeded == false){
        text = error;
        if (lineOfError != -1){
            showLineWithError(lineOfError);
        }
        $("#compileResponse")[0].style.backgroundColor = "#ffcccc";
        $("#labelWithResponse")[0].style.color = "red";
    } else {
        var outputOkVal = "Output: " + outputOk;
        text = response;
        $("#labelWithResponse")[0].style.color = "green";
        $("#compileResponse")[0].style.backgroundColor = "#d9ffcc";
    }

    if (outputOkVal === false){
        swal("Sorry!", "The returned value is not the expected one", "error");
    } else if (outputOkVal === true){
        swal("Good job!", "Success!", "success");
    }

    $("#compileResponse").val(text);
}

function showLineWithError(line) {
    var number = parseInt(line-1);
    editor.markText({line: number, ch: 0}, {line: number, ch: 200}, {
        className: "styled-background-error"
    });

    editor.on("mousedown", function(editor, e) {
        $(".styled-background-error").removeClass("styled-background-error");
    });
}

function showCreators() {
    swal({
        title: "Created by:",
        text: "Piotr Karaś \n Patrycja Kopacz",
        imageUrl: 'images/icon.png'
    });
}