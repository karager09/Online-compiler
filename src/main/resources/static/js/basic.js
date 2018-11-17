var editor = null;

CodeMirror.commands.autocomplete = function(cm) {
    cm.showHint({hint: CodeMirror.hint.anyword});
}


$(document).ready(function(){
    createEditor();
    getListOfExercises();
});

function createEditor() {
    var code = $(".codemirror-textarea")[0];
    editor = CodeMirror.fromTextArea(code,{
        lineNumbers : true,
        lineWrapping: true,
        addModeClass: true,
        styleActiveLine: true,
        matchBrackets: true,
        autoCloseBrackets : true,
        showTrailingSpace: true,
        mode : "clike",
        dragDrop : false,
        fullScreen : false,
        highlightSelectionMatches: {showToken: /\w/, annotateScrollbar: true},
        extraKeys: {
            "Esc": function(cm) {
                if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
            },
            "Ctrl-L": function(cm){
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
    for (var i = 0; i< lines.length; i++ ){
        editor.markText({line: i, ch: 0}, {line: i, ch: 200}, {readOnly: true, className: "styled-background"});
    }
}

function selectLanguage(){
    var input = document.getElementById("selectLanguage");
    // todo po zmienie zmien mode, tylko nie wiesz jakie jezyki wiec nie ma mode
    // todo nic sie nie dzieje jeszcze XD
    // setLanguage(language)
}

function setLanguage(language){
    editor.setOption("mode", language);
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
            reader.onload = function(e) {
                var text = reader.result;
                editor.setValue(text);
            }
            reader.readAsText(file);
        }
    }
}

function saveCodeToFile() {
    // todo you can specify filename and data type and file extension
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
    // todo you can specify filename
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
    CodeMirror.on(window, "hashchange", function() {
        var theme = location.hash.slice(1);
        if (theme) { input.value = theme; selectTheme(); }
    });
}
function fullscreen(){
    // todo add alert with information about closing fullscreen
    if (editor.getOption("fullScreen")) {
        editor.setOption("fullScreen", false);
    } else {
        editor.setOption("fullScreen", true);
    }
}


function parseJsonWithExercisesList(returnMessage) {
    var list = $("#exercisesList");
    var obj = JSON.parse(returnMessage);

    for (var j=0; j< obj.length; j++){
        var language = obj[j].language;

        var button = $('<button type="button" class="list-group-item disabled">' + language + '</button>');
        list.append(button);

        var elements = obj[j].elements;
        for (var i = 0; i< elements.length ; i++){
            var name = elements[i].name;
            var id = elements[i].id;

            var button = $('<button type="button" class="list-group-item" onclick="getExercise('+ id +')" id="' + id +'">' + name + '</button>');
            list.append(button);
        }
    }
}

function clearEditor() {
    editor.setValue("");
}

function parseJsonWithExercise(exercise) {
    console.log(exercise);

    console.log(exercise);
    var obj = JSON.parse(exercise);

    var name = obj.name;
    var id = obj.id;
    var code = obj.code;
    var inactive = obj.inactive;
    var contents = obj.contents;
    var hints = obj.hints;
    var language = obj.language;


    makeReadOnly(inactive);
    setLanguage(language);
    editor.setValue(code);

    // todo contents trzeba pokazac w popupie oraz na stronie
    // todo name gdzies wyswietlic
    // todo id zapisac w jakiejs zmiennej
    // todo hints - button do wyswietlania
    // todo dodaj nowa fk do wyslania do kompilatora albo button czyli zmien przycisk zeby
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

function sendCode(){

    var code = editor.getValue();
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