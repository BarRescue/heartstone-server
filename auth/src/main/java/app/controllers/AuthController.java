package app.controllers;

import app.controllers.enums.Response;
import app.controllers.enums.Role;
import app.entity.User;
import app.helpers.PasswordHelper;
import app.jwt.TokenProvider;
import app.models.AuthorisationModel;
import app.models.UserRegisterModel;
import app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final PasswordHelper passwordHelper;

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(UserService userService, TokenProvider tokenProvider, PasswordHelper passwordHelper) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordHelper = passwordHelper;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthorisationModel authModel) {
        User user = userService.findByEmail(authModel.getEmail())
                .orElseThrow(() -> { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Response.WRONG_CREDENTIALS.toString()); });

        if (!passwordHelper.isMatch(authModel.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Response.WRONG_CREDENTIALS.toString());
        }

        try {
            Map<Object, Object> model = new LinkedHashMap<>();
            model.put("token", tokenProvider.createToken(user.getId(), user.getRole(), user.getFirstName(), user.getLastName()));
            model.put("user", user);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Response.UNEXPECTED_ERROR.toString());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity register (@Valid @RequestBody UserRegisterModel registerModel) {
        if (userService.findByEmail(registerModel.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Response.USER_ALREADY_EXISTS.toString());
        }

        try {
            User user = new User();

            user.setEmail(registerModel.getEmail());
            user.setFirstName(registerModel.getFirstName());
            user.setLastName(registerModel.getLastName());
            user.setPassword(passwordHelper.hash(registerModel.getPassword()));
            user.setRole(Role.USER);

            User createdUser = userService.createOrUpdate(user);

            Map<Object, Object> model = new LinkedHashMap<>();
            model.put("token", tokenProvider.createToken(createdUser.getId(), createdUser.getRole(), createdUser.getFirstName(), createdUser.getLastName()));
            model.put("user", createdUser);
            return ok(model);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Response.UNEXPECTED_ERROR.toString());
        }
    }
}
