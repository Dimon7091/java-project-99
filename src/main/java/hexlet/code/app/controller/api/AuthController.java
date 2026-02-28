package hexlet.code.app.controller.api;

import hexlet.code.app.dto.AuthRequest;
import hexlet.code.app.model.User.User;
import hexlet.code.app.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String createJWT(@RequestBody AuthRequest authRequest) {
        // СОХРАНЯЕМ результат аутентификации
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );
        // Теперь в authentication есть principal!
        User user = (User) authentication.getPrincipal();

        // Генерируем токен
        return jwtUtils.generateToken(user);
    }

}
