// todo wyswietlanie wynikow kompilacji, bledow, wyniku programu


var editor = null;
var taskId = -1;

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

    loadTheme();
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

function selectLanguage() {
    var input = $("#selectLanguage")[0].value;
    setLanguage(input);
}

function setLanguage(language) {
    language = language.toLowerCase();
    if (language == "java") {
        editor.setOption("mode", "text/x-java");
    } else if (language == "cpp") {
        editor.setOption("mode", "text/x-c++src");
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

function selectTheme() {
    var input = document.getElementById("selectTheme");
    var theme = input.options[input.selectedIndex].textContent;
    editor.setOption("theme", theme);
    location.hash = "#" + theme;
}

function loadTheme() {
    var choice = (location.hash && location.hash.slice(1)) || (document.location.search && decodeURIComponent(document.location.search.slice(1)));
    var input = document.getElementById("selectTheme");

    if (choice) {
        input.value = choice;
        editor.setOption("theme", choice);
    }
    CodeMirror.on(window, "hashchange", function () {
        var theme = location.hash.slice(1);
        if (theme) {
            input.value = theme;
            selectTheme();
        }
    });
}

function fullscreen() {
    if (editor.getOption("fullScreen")) {
        editor.setOption("fullScreen", false);
    } else {
        editor.setOption("fullScreen", true);
        alert("To close fullscreen mode click ESC");
    }
}


function parseJsonWithExercisesList(returnMessage) {
    var list = $("#exercisesList");
    var obj = JSON.parse(returnMessage);

    for (var j = 0; j < obj.length; j++) {
        var language = obj[j].language;

        var button = $('<button type="button" class="list-group-item disabled">' + language + '</button>');
        list.append(button);

        var elements = obj[j].elements;
        for (var i = 0; i < elements.length; i++) {
            var name = elements[i].name;
            var id = elements[i].id;

            var button = $('<button type="button" class="list-group-item" onclick="getExercise(' + id + ')" id="' + id + '">' + name + '</button>');
            list.append(button);
        }
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
    $("#selectLanguage")[0].value = language.toLowerCase();
    editor.setValue(code);
    makeReadOnly(inactive);

    taskId = parseInt(id);

    var compilerModeDescription = $("#compilerModeDescription")[0];
    compilerModeDescription.textContent = name.toUpperCase() + " - " + contents;

    alert(name + "\n" + contents);

    $("#showHintButton").unbind('click');
    $("#showHintButton").on("click", function () {
        showHints(hints);
    });
}

function clearEditor() {
    editor.setValue("");

    var compilerModeDescription = $("#compilerModeDescription")[0];
    compilerModeDescription.textContent = "Editor";

    taskId = -1;

    $("#showHintButton").unbind('click');
}

function showHints(hints) {
    var random = Math.floor(Math.random() * (+hints.length - +0)) + +0;
    alert(hints[random]);
}


function getExercise(id) {
    $.ajax({
        url: "http://localhost:8080/api/exercise/" + id,
        datatype: 'json',
        type: "get"
    }).then(function (data, status, jqxhr) {
        console.log(data)
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
    var runCompiledProgramCheckboxValue = $("#runCompiledProgramCheckbox")[0].checked;
    var runCompiledProgram = (runCompiledProgramCheckboxValue == 'true');
    var language = $("#selectLanguage")[0].value;

    // if (taskId == -1) {
    //     $.ajax({
    //         url: "http://localhost:8080/api/compile/code",
    //         datatype: 'json',
    //         type: "post",
    //         contentType: "application/json",
    //         data: JSON.stringify({
    //             code: code,
    //             input: input,
    //             language: language,
    //             runCompiledProgram : runCompiledProgram
    //         })
    //     }).then(function (data, status, jqxhr) {
    //
    //         $("#compileResponse").val('Status success, Server response: \n' + data);
    //     });
    // } else {
    $.ajax({
        url: "http://localhost:8080/api/compile/code",
        datatype: 'json',
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({
            code: code,
            input: input,
            language: language,
            runCompiledProgram: runCompiledProgram,
            taskId: taskId
        })
    }).then(function (data, status, jqxhr) {
        //todo zmien sobie zeby jakoś to się ładnie pokazywało :P
        //jak cos to odejmij 1 od lini z problemem (domyślnie zaczynają się od 0)
        var obj = JSON.parse(data);
        $("#compileResponse").val(obj.compilationSucceeded + '\n' + obj.lineOfError + '\n' + obj.outputOk + '\n' + obj.response + '\n' + obj.error);
    });
    // }
}