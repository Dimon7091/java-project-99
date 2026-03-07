package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.label.Label;
import hexlet.code.app.model.user.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.JWTUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"dev", "test"})
public class LabelApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    private Label testLabel;
    private Label anotherLabel;
    private User testUser;
    private String userToken;

    @BeforeEach
    public void setUp() {
        labelRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем тестового пользователя
        testUser = User.builder()
                .email("user@example.com")
                .passwordDigest(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("user")
                .build();
        userRepository.save(testUser);
        userToken = jwtUtils.generateToken(testUser);

        // Создаем тестовый лейбел
        testLabel = Label.builder()
                .name("test Label")
                .build();
        anotherLabel = Label.builder()
                .name("teest Label")
                .build();
        labelRepository.save(testLabel);
        labelRepository.save(anotherLabel);
    }

    @Test
    @DisplayName("POST Создание лейбла авторизованным пользователем (статус 201)")
    public void postLabel_Authenticated_ShouldRetorn201() throws Exception {
        var labelData = Map.of(
                "name", "some label"
        );

        mockMvc.perform(post("/api/labels")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(labelData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(labelData.get("name")));
    }

    @Test
    @DisplayName("GET получение списка лейблов авторизованным пользователем (статус 200)")
    public void indexLabels_Authenticated_ShouldRetorn200() throws Exception {
        mockMvc.perform(get("/api/labels")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET получение лейбла по id авторизованным пользователем (статус 200)")
    public void showLabel_Authenticated_ShouldRetorn200() throws Exception {
        mockMvc.perform(get("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testLabel.getName()));
    }

    @Test
    @DisplayName("PUT обновление лейбла авторизованным пользователем (статус 200)")
    public void putLabel_Authenticated_ShouldRetorn200() throws Exception {
        var updatedData = Map.of(
                "name", "some label"
        );

        mockMvc.perform(put("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedData.get("name")));
    }

    @Test
    @DisplayName("DELETE успешное удаление лейбола (статус 204)")
    public void deleteLabel_Authenticated_ShouldRetorn204() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE не успешное удаление лейбола, не авторизованным пользователем (статус 403)")
    public void deleteLabel_Unauthenticated_ShouldRetorn403() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }
}
