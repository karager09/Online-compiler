package server;

import com.fasterxml.jackson.databind.util.JSONPObject;
import compiler.Code;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.ws.Response;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    /**
     * Obsługa wyjątków rzucanych przez metody REST
     *
     * @return zwraca pusty model i wodok, łapany w kodzie JS
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
    public ResponseEntity getExercicesList() throws JSONException {
        JSONArray json = new JSONArray();

        JSONObject language = new JSONObject();
        language.put("language", "java");

        JSONArray array = new JSONArray();
        JSONObject exer = new JSONObject();
        exer.put("id", 0);
        exer.put("name", "reksio");
        array.put(exer);

        JSONObject exer2 = new JSONObject();
        exer2.put("id", 1);
        exer2.put("name", "bruno");
        array.put(exer2);

        language.put("elements", array);

        json.put(language);

        return new ResponseEntity(json.toString(), HttpStatus.ACCEPTED);

    }

    @GetMapping("api/exercise/{id}") // todo zmien to, mi się nie chcialo bawic XD
    public ResponseEntity getExercice(@PathVariable("id") int id) throws JSONException {
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
        exer.put("code", "dfhdfghdfh");
        exer.put("hints", hints);
        exer.put("inactive", lines);
        return new ResponseEntity(exer.toString(), HttpStatus.ACCEPTED);
    }
}