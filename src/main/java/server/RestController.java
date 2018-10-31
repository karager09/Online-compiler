package server;

import compiler.Code;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    /**
     * Obsługa wyjątków rzucanych przez metody REST
     * @return zwraca pusty model i wodok, łapany w kodzie JS
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleError() {
        return new ModelAndView();
    }

    @PostMapping("/api/compile/code")
    public String sayHello(@RequestBody Code receivedCode){
        System.out.println(receivedCode.getCode());
        return "I would compile this.. the other day.";
    }
}
