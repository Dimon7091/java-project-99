package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.User.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.JWTUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Transactional
public class UserApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    private User testUser;
    private User anotherUser;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Создаем тестового пользователя
        testUser = User.builder()
                .email("user@example.com")
                .passwordDigest(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .build();
        userRepository.save(testUser);

        // Создаем другого пользователя
        anotherUser = User.builder()
                .email("another@example.com")
                .passwordDigest(passwordEncoder.encode("password123"))
                .firstName("Another")
                .lastName("User")
                .build();
        userRepository.save(anotherUser);

        // Генерируем токен для тестового пользователя
        userToken = jwtUtils.generateToken(testUser);
    }

    // ===== ТЕСТЫ =====

    @Nested
    @DisplayName("Тесты публичных эндпоинтов")
    class PublicEndpoints {

        @Test
        @DisplayName("Регистрация нового пользователя - 201 Created")
        void createUser_ValidData_ShouldReturn201() throws Exception {
            var newUser = Map.of(
                    "email", "new@example.com",
                    "password", "password123",
                    "firstName", "New",
                    "lastName", "User"
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("new@example.com"))
                    .andExpect(jsonPath("$.firstName").value("New"))
                    .andExpect(jsonPath("$.password").doesNotExist()); // Пароль не возвращаем
        }

        @Test
        @DisplayName("Регистрация с существующим email - 409 Conflict")
        void createUser_DuplicateEmail_ShouldReturn409() throws Exception {
            var duplicateUser = Map.of(
                    "email", testUser.getEmail(),
                    "password", "password123",
                    "firstName", "New",
                    "lastName", "User"
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateUser)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Регистрация с невалидными данными - 422 Unprocessable Entity")
        void createUser_InvalidData_ShouldReturn422() throws Exception {
            var invalidUser = Map.of(
                    "email", "not-an-email",
                    "password", "12"  // слишком короткий
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidUser)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    // === Защищенные маршруты ===

    @Nested
    @DisplayName("Тесты защищенных эндпоинтов")
    class ProtectedEndpoints {

        @Test
        @DisplayName("GET /api/users - успешное получение списка (аутентифицирован)")
        void getUsers_Authenticated_ShouldReturn200() throws Exception {
            mockMvc.perform(get("/api/users")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].email").value(testUser.getEmail()))
                    .andExpect(jsonPath("$[1].email").value(anotherUser.getEmail()));
        }

        @Test
        @DisplayName("GET /api/users - без токена (401 Unauthorized)")
        @WithAnonymousUser
        void getUsers_WithoutToken_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/users - с невалидным токеном (401 Unauthorized)")
        void getUsers_InvalidToken_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/users")
                            .header("Authorization", "Bearer invalid.token.here"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/users/{id} - свой профиль (200 OK)")
        void getUserById_OwnProfile_ShouldReturn200() throws Exception {
            mockMvc.perform(get("/api/users/{id}", testUser.getId())
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testUser.getId()))
                    .andExpect(jsonPath("$.email").value(testUser.getEmail()));
        }

        @Test
        @DisplayName("GET /api/users/{id} - чужой профиль (403 Forbidden)")
        void getUserById_OtherUser_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/{id}", anotherUser.getId())
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/users/{id} - обновление своего профиля (200 OK)")
        void fullUpdateUser_OwnProfile_ShouldReturn200() throws Exception {
            var updates = Map.of(
                    "firstName", "Updated",
                    "lastName", "Name",
                    "email", "updated@example.com",
                    "password", "newpassword123"
            );

            mockMvc.perform(put("/api/users/{id}", testUser.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.email").value("updated@example.com"));

            // Проверяем, что данные действительно обновились в БД
            User updatedUser = userRepository.findById(testUser.getId()).get();
            assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        }

        @Test
        @DisplayName("PATCH /api/users/{id} - частичное обновление своего профиля (200 OK)")
        void partialUpdateUser_OwnProfile_ShouldReturn200() throws Exception {
            var updates = Map.of(
                    "lastName", "Updated"
            );

            mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lastName").value(updates.get("lastName")));

            // Проверяем, что данные действительно обновились в БД
            User updatedUser = userRepository.findById(testUser.getId()).get();
            assertThat(updatedUser.getLastName()).isEqualTo(updates.get("lastName"));
        }

        @Test
        @DisplayName("DELETE /api/users/{id} - удаление своего профиля (204 No Content)")
        void deleteUser_OwnProfile_ShouldReturn204() throws Exception {
            mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNoContent());

            // Проверяем, что пользователь действительно удален
            assertThat(userRepository.findById(testUser.getId())).isEmpty();
        }
    }

      // Для будущего функционалла ролей

//    @Nested
//    @DisplayName("Тесты с разными ролями")
//    class RoleBasedTests {
//
//        @BeforeEach
//        void setUpAdmin() {
//            // Создаем админа
//            User admin = User.builder()
//                    .email("admin@example.com")
//                    .passwordDigest(passwordEncoder.encode("admin123"))
//                    .build();
//            userRepository.save(admin);
//        }
//
//        @Test
//        @DisplayName("Админ может удалять любого пользователя")
//        void adminCanDeleteAnyUser() throws Exception {
//            // Генерируем токен для админа
//            User admin = userRepository.findByEmail("admin@example.com").get();
//            String adminToken = jwtUtils.generateToken(admin);
//
//            mockMvc.perform(delete("/api/users/{id}", anotherUser.getId())
//                            .header("Authorization", "Bearer " + adminToken))
//                    .andExpect(status().isNoContent());
//        }
//    }
}
