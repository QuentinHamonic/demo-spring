package fr.diginamic.hello.controleurs;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/hello")
public class HelloControleur {

    @GetMapping
    public String direHello() {
        return "Hello";
    }

}
