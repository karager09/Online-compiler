package server;

import com.fasterxml.jackson.databind.util.JSONPObject;
import compiler.Code;
import files.FileManager;
import files.TaskFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

//import javax.xml.ws.Response;

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
    public String sayHello(@RequestBody Code receivedCode) {
        //System.out.println(receivedCode.getCode() +", " + receivedCode.getLanguage());
        return receivedCode.run();
//        return "I would compile this.. the other day.";
    }

    @GetMapping("/api/exercise/list") // todo zmien to, mi się nie chcialo bawic XD
    public ResponseEntity getExercisesList() throws JSONException {
        JSONArray json = new JSONArray();
//
//        JSONObject language = new JSONObject();
//        language.put("language", "cppX");
//
//        JSONArray array = new JSONArray();
//        JSONObject exer = new JSONObject();
//        exer.put("id", 0);
//        exer.put("name", "reksio");
//        array.put(exer);
//
//        JSONObject exer2 = new JSONObject();
//        exer2.put("id", 1);
//        exer2.put("name", "bruno");
//        array.put(exer2);
//
//        language.put("elements", array);
//        json.put(language);

        for (String language : FileManager.get().getLanguages()) {
            JSONObject languageAndTasks = new JSONObject();
            languageAndTasks.put("language", language);
            JSONArray array = new JSONArray();

            for (TaskFile taskFile : FileManager.get().getTasksForLanguage(language)) {
                JSONObject exer = new JSONObject();
                System.out.println(taskFile.getId());
                exer.put("name", taskFile.getName());
                array.put(exer);
            }
            languageAndTasks.put("elements", array);
            json.put(languageAndTasks);
        }
        return new ResponseEntity(json.toString(), HttpStatus.ACCEPTED);

    }

    @GetMapping("api/exercise/1") // todo zmien to, mi się nie chcialo bawic XD
    public ResponseEntity getExercice() throws JSONException {
        JSONArray hints = new JSONArray();
        hints.put("aaaa");
        hints.put("asdsagadhah");
        JSONArray lines = new JSONArray();
        lines.put(1);
        lines.put(4);
        JSONObject exer = new JSONObject();
        exer.put("id", 0);
        exer.put("name", "reksio");
        exer.put("language", "java");
        exer.put("contents", "dsdsdgsgs");
        exer.put("code", "#include &ltiostream&gt\n" +
                "using namespace std;\n" +
                "int main()\n" +
                "{\n" +
                "    iaaant x;\n" +
                "    coutvvv << \"Hello\" << endl;\n" +
                "    cinvvv >> x;\n" +
                "    cout vvv<< x << endl;\n" +
                "    cout &vvddlt&lt \"world\";\n" +
                "    returdddn 0;\n" +
                "}");
        exer.put("hints", hints);
        exer.put("inactive", lines);
        return new ResponseEntity(exer.toString(), HttpStatus.ACCEPTED);
    }

    @GetMapping("api/exercise/0") // todo zmien to, mi się nie chcialo bawic XD
    public ResponseEntity getExercice2() throws JSONException {
        JSONArray hints = new JSONArray();
        hints.put("aaaa");
        hints.put("asdsagadhah");
        JSONArray lines = new JSONArray();
        lines.put(8);
        lines.put(7);
        JSONObject exer = new JSONObject();
        exer.put("id", 0);
        exer.put("name", "reksio");
        exer.put("language", "java");
        exer.put("contents", "dsdsdgsgs");
        exer.put("code", "#include &ltiostream&gt\n" +
                "using namespace std;\n" +
                "int main()\n" +
                "{\n" +
                "    sasasasiaaant x;\n" +
                "    asasasascoutvvv << \"Hello\" << endl;\n" +
                "    sasassacinvvv >> x;\n" +
                "    cout sasasavvv<< x << endl;\n" +
                "    cout &vvddlasasat&lt \"world\";\n" +
                "    retuasasasasasrdddn 0;\n" +
                "}");
        exer.put("hints", hints);
        exer.put("inactive", lines);
        return new ResponseEntity(exer.toString(), HttpStatus.ACCEPTED);
    }
}