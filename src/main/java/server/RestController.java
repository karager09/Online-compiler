package server;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping
    public String sayHello(){
        return "Hello!";
    }
}
