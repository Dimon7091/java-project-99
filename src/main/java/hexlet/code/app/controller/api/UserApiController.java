package hexlet.code.app.controller.api;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.dto.userDTO.UserDTO;
import hexlet.code.app.dto.userDTO.UserFullUpdateDTO;
import hexlet.code.app.dto.userDTO.UserPartialUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper mapper;

    @PostMapping("")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO userData) {
        var savedUser = userService.create(userData);
        return ResponseEntity.created(URI.create("api/users/" + savedUser.id()))
                .body(savedUser);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index(@RequestParam Integer _start,
                                               @RequestParam Integer _end,
                                               @RequestParam String _sort,
                                               @RequestParam String _order) {
        int page = _start / (_end - _start);
        int size = _end - _start;
        Sort.Direction direction = _order.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, _sort));
        var responseData = userService.findAll(pageable);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(responseData.totalUsers()))
                .body(responseData.userDTOList());
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
