package hexlet.code.app.controller.ApiController;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.dto.userDTO.UserDTO;
import hexlet.code.app.dto.userDTO.UserFullUpdateDTO;
import hexlet.code.app.dto.userDTO.UserPartialUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

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

    @GetMapping("{id}")
    public ResponseEntity<UserDTO> show(@PathVariable("id") Long id) {
        var user = userService.findById(id);
        return ResponseEntity.ok()
                .body(user);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDTO> fullUpdate(@PathVariable("id") Long id, @RequestBody UserFullUpdateDTO userData) {
        var updatedUser = userService.fullUpdate(id, userData);
        return ResponseEntity.ok()
                .body(updatedUser);
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserDTO> partialUpdate(@PathVariable("id") Long id, @RequestBody UserPartialUpdateDTO userData) {
        var updatedUser = userService.partialUpdate(id, userData);
        return ResponseEntity.ok()
                .body(updatedUser);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}
