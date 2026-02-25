package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.User.User;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    // Позитивные тесты
    @Test
    void testCreate_whenReturn_201() throws Exception {
        var userData = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password" ,faker.random().toString()
        );

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userData.get("email")));
    }

    @Test
    void testShow_whenReturn_200() throws Exception {
        var user = createUser();
        var savedUser = userRepository.save(user);

        var request = get("/api/users/" + savedUser.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()));
    }

    @Test
    void testIndex_whenReturn_200() throws Exception {
        var users = List.of(createUser(), createUser(), createUser());
        userRepository.saveAll(users);

        var request = get("/api/users");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(users.size())))
                .andExpect(jsonPath("$.[0].email").value(users.get(0).getEmail()))
                .andExpect(jsonPath("$.[1].email").value(users.get(1).getEmail()))
                .andExpect(jsonPath("$.[2].email").value(users.get(2).getEmail()));
    }

    @Test
    void testPutUpdate_whenReturn_200() throws Exception {
        var user = createUser();
        var savedUser = userRepository.save(user);

        var userUpdateData = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password" ,faker.random().toString()
        );

        var request = put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userUpdateData));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userUpdateData.get("email")))
                .andExpect(jsonPath("$.firstName").value(userUpdateData.get("firstName")))
                .andExpect(jsonPath("$.lastName").value(userUpdateData.get("lastName")));
    }

    @Test
    void testPatch_whenReturn_204() throws Exception {
        var user = createUser();
        var savedUser = userRepository.save(user);

        var userUpdateData = Map.of(
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName()
        );

        var request = patch("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userUpdateData));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userUpdateData.get("firstName")))
                .andExpect(jsonPath("$.lastName").value(userUpdateData.get("lastName")));
    }

    @Test
    void testDelete_whenReturn_204() throws Exception {
        var user = createUser();
        var savedUser = userRepository.save(user);

        var request = delete("/api/users/" + savedUser.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    // Негативные тесты
    @Test
    void testCreate_withInvalidEmail_whenReturn_400() throws Exception {
        var userData = Map.of(
                "email", "invalid-email",
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password" ,faker.random().toString()
        );

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    // Негативные тесты
    @Test
    void testCreate_withDuplicateEmail_whenReturn_409() throws Exception {
        var user = createUser();
        var savedUser = userRepository.save(user);

        var userData = Map.of(
                "email", savedUser.getEmail(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password" ,faker.random().toString()
        );

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    void testCreate_withMissingFields_whenReturn_400() throws Exception {
        var userData = Map.of(
                "email", faker.internet().emailAddress(),
                "password" ,faker.random().toString()
        );

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testPatchUpdate_withNonExistentId_whenReturn_404() throws Exception {
        var userData = Map.of(
                "email", faker.internet().emailAddress(),
                "password" ,faker.random().toString()
        );

        var request = patch("/api/users/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }



    private User createUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getCreatedAt))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getPasswordDigest), () -> faker.lorem().characters(6))
                .create();
    }
}
