package server;

import compiler.Code;
import compiler.CodeResult;
import files.FileManager;
import files.TaskFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@org.springframework.web.bind.annotation.RestController
public class RestController {


    /**
     * Obsługa wyjątków rzucanych przez metody REST
     *
     * @return zwraca pusty model i widok, łapany w kodzie JS
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleError() {
        return new ModelAndView();
    }

    @PostMapping("/api/compile/code")
    public ResponseEntity sayHello(@RequestBody Code receivedCode) throws JSONException {
        CodeResult result = receivedCode.run();

        JSONObject json = new JSONObject();
        json.put("compilationSucceeded", result.getCompilationSucceeded());
        json.put("lineOfError", result.getLineOfError());
        json.put("response", result.getResponse());
        json.put("error", result.getError());
        json.put("outputOk", result.isOutputOk());

        return new ResponseEntity(json.toString(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/api/exercise/list")
    public ResponseEntity getExercisesList() throws JSONException {
        JSONArray json = new JSONArray();

        for (String language : FileManager.get().getLanguages()) {
            JSONObject languageAndTasks = new JSONObject();
            languageAndTasks.put("language", language);
            JSONArray array = new JSONArray();

            for (TaskFile taskFile : FileManager.get().getTasksForLanguage(language)) {
                JSONObject exer = new JSONObject();
                exer.put("id", taskFile.getId());
                exer.put("name", taskFile.getName());
                array.put(exer);
            }
            languageAndTasks.put("elements", array);
            json.put(languageAndTasks);
        }
        return new ResponseEntity(json.toString(), HttpStatus.ACCEPTED);
    }

    @GetMapping("api/exercise/{id}")
    public ResponseEntity getExercise(@PathVariable("id") Integer id) throws JSONException {
        TaskFile task = FileManager.get().getTaskById(id);
        if (task == null) {
            // todo co zwrócić jak podane zostało złe id?
        }

        JSONArray hints = new JSONArray();
        if (task.getHints() != null)
            for (String hint : task.getHints())
                hints.put(hint);

        JSONArray lines = new JSONArray();
        if (task.getLines() != null)
            for (int line : task.getLines())
                lines.put(line);

        JSONObject exer = new JSONObject();
        exer.put("id", task.getId());
        exer.put("name", task.getName());
        exer.put("language", task.getLanguage());
        exer.put("contents", task.getContents());
        exer.put("code", task.getCode());
        exer.put("hints", hints);
        exer.put("inactive", lines);

        return new ResponseEntity(exer.toString(), HttpStatus.ACCEPTED);
    }
}