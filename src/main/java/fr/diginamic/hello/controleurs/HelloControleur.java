package fr.diginamic.hello.controleurs;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.diginamic.hello.services.HelloService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/hello")
public class HelloControleur {

    private HelloService helloService;

    public HelloControleur(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping
    public String direHello() {
        return helloService.salutations();
    }

}
