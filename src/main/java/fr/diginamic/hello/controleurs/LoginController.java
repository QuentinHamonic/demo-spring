package fr.diginamic.hello.controleurs;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.diginamic.hello.dto.LoginRequest;
import fr.diginamic.hello.security.JwtUtil;

@RestController
public class LoginController {

    private final AuthenticationConfiguration config;

    private final JwtUtil jwtUtil;

    LoginController(AuthenticationConfiguration config, JwtUtil jwtUtil) {
        this.config = config;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) throws Exception {

        config.getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.username(),
                        req.password()));

        return jwtUtil.generateToken(req.username());
    }
}