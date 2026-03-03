package hexlet.code.app.controller.api;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.dto.userDTO.UserDTO;
import hexlet.code.app.dto.userDTO.UserFullUpdateDTO;
import hexlet.code.app.dto.userDTO.UserPartiallyUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<List<UserDTO>> index(
            @RequestParam(name = "_start", defaultValue = "0") int start,  // маппинг имени
            @RequestParam(name = "_end", defaultValue = "10") int end,
            @RequestParam(name = "_sort", defaultValue = "id") String sort,
            @RequestParam(name = "_order", defaultValue = "ASC") String order) {
        int page = start / (end - start);
        int size = end - start;
        Sort.Direction direction = order.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
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
                                                 @RequestBody UserPartiallyUpdateDTO userData) {
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
