package hexlet.code.app.controller.api;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.dto.userDTO.UserDTO;
import hexlet.code.app.dto.userDTO.UserFullUpdateDTO;
import hexlet.code.app.dto.userDTO.UserPartialUpdateDTO;
import hexlet.code.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO userData) {
        var savedUser = userService.create(userData);
        return ResponseEntity.created(URI.create("api/users/" + savedUser.id()))
                .body(savedUser);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index() {
        var users = userService.findAll();
        return ResponseEntity.ok()
                .body(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.claims['userId']")
    public ResponseEntity<UserDTO> show(@PathVariable("id") Long id) {
        var user = userService.findById(id);
        return ResponseEntity.ok()
                .body(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.claims['userId']")
    public ResponseEntity<UserDTO> fullUpdate(@PathVariable("id") Long id, @RequestBody UserFullUpdateDTO userData) {
        var updatedUser = userService.fullUpdate(id, userData);
        return ResponseEntity.ok()
                .body(updatedUser);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.claims['userId']")
    public ResponseEntity<UserDTO> partialUpdate(@PathVariable("id") Long id,
                                                 @RequestBody UserPartialUpdateDTO userData) {
        var updatedUser = userService.partialUpdate(id, userData);
        return ResponseEntity.ok()
                .body(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("#id == authentication.principal.claims['userId']")
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}
