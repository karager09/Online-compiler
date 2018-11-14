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

    var readOnlyLines = [0,1,2,3];
    editor.on('beforeChange',function(cm,change) {
        if ( ~readOnlyLines.indexOf(change.from.line) ) {
            change.cancel();

        }
    });
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
    // todo zparsuj to co dostaniesz od Piotrka jako liste
    // todo tzn od razu wyswietl w menu w sumie
    // dodaj gdzies przycisk zeby korzystac z czystego kompilatora a nie zadan
}


function parseJsonWithExercise(exercise) {
    // todo zparsuj to co dostaniesz od Piotrka jako zadanie
    // todo tzn od razu wywietl w sumie
    // todo dostaniesz id, nazwa, tresc, jezyk, kod do edytora, linie nieaktywne, wskazowki
    // todo dodaj nowy ajax() czyli zmien przycisk zeby
    // trzeba zmienic jezyk i linie dezaktywowac
}

// on click exercise w menu
// todo w menu po kliknieciu na zadanie pobieram dane
function getExercise(id) {
    var exercise = null;
    $.ajax({
        url: "http://localhost:8080/api/exercise/" + id ,
        datatype: 'json',
        type: "get"
    }).then(function (data, status, jqxhr) {
        exercise = data;
    });

    parseJsonWithExercise(exercise);

}

function getListOfExercises() {
    var returnMessage = null;
    $.ajax({
        url: "http://localhost:8080/api/exercise/list",
        datatype: 'json',
        type: "get"
    }).then(function (data, status, jqxhr) {
       returnMessage = data;
    });

    parseJsonWithExercisesList(returnMessage);

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