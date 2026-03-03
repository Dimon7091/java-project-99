package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.taskStatus.TaskStatus;
import hexlet.code.app.model.user.User;
import hexlet.code.app.repository.TaskStatusRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"dev", "test"})
public class TaskStatusApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    private String userToken;
    private User testUser;
    private TaskStatus testStatus;
    private TaskStatus anotherStatus;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();

        // Создаем тестового пользователя
        testUser = User.builder()
                .email("user@example.com")
                .passwordDigest(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("user")
                .build();
        userRepository.save(testUser);

        userToken = jwtUtils.generateToken(testUser);

        // Создаем тестовые статусы
        testStatus = TaskStatus.builder()
                .name("some status")
                .slug("some_status")
                .build();
        anotherStatus = TaskStatus.builder()
                .name("another status")
                .slug("another_status")
                .build();

        taskStatusRepository.save(testStatus);
        taskStatusRepository.save(anotherStatus);
    }

    // ===== ТЕСТЫ =====

    @Nested
    @DisplayName("Тесты c залогиненным пользователем (с токеном)")
    class WithJwtToken {

        @Test
        @DisplayName("POST/api/task_statuses - успешное создание статуса (аунтефицирован 201)")
        public void postTaskStatuses_Authenticated_ShouldRetorn201() throws Exception {
            var taskStatus = Map.of(
                    "name", "newTask",
                    "slug", "new_task"
            );

            mockMvc.perform(post("/api/task_statuses")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskStatus)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.slug").exists());
        }

        @Test
        @DisplayName("GET/api/task_statuses - успешное получение списка статусов (аунтефицирован 200)")
        public void getTaskStatuses_Authenticated_ShouldRetorn200() throws Exception {
            mockMvc.perform(get("/api/task_statuses")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET/api/task_statuses - успешное получение статуса по ID (аунтефицирован 200)")
        public void getTaskStatusById_Authenticated_ShouldRetorn200() throws Exception {
            mockMvc.perform(get("/api/task_statuses/" + testStatus.getId())
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testStatus.getId()))
                    .andExpect(jsonPath("$.name").value(testStatus.getName()));
        }

        @Test
        @DisplayName("PUT/api/task_statuses - успешное полное обновление статуса (аунтефицирован 200)")
        public void putTaskStatuses_Authenticated_ShouldRetorn200() throws Exception {
            var updateStatus = Map.of(
                    "name", "updated status",
                    "slug", "updated_status"
            );

            mockMvc.perform(put("/api/task_statuses/" + testStatus.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStatus)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(updateStatus.get("name")))
                    .andExpect(jsonPath("$.slug").value(updateStatus.get("slug")));
        }

        @Test
        @DisplayName("PATCH/api/task_statuses - успешное частичное обновление статуса (аунтефицирован 200)")
        public void patchTaskStatuses_Authenticated_ShouldRetorn200() throws Exception {
            var updateStatus = Map.of(
                    "name", "updated status"
            );

            mockMvc.perform(patch("/api/task_statuses/" + testStatus.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStatus)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(updateStatus.get("name")));
        }

        @Test
        @DisplayName("DELETE/api/task_statuses - успешное удаление статуса (аунтефицирован 204)")
        public void deleteTaskStatuses_Authenticated_ShouldRetorn204() throws Exception {
            mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId())
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNoContent());
        }

        // ===== Тесты с невалидными данными =====

        @Test
        @DisplayName("POST/api/task_statuses - создание статуса с пустым именем (аунтефицирован 422)")
        public void postTaskStatuses_Authenticated_ShouldRetorn422() throws Exception {
            var taskStatus = Map.of(
                    "name", "",
                    "slug", "new_task"
            );

            mockMvc.perform(post("/api/task_statuses")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskStatus)))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("POST/api/task_statuses - создание статуса с существующим слагом (аунтефицирован 409)")
        public void postTaskStatuses_Authenticated_ShouldRetorn409() throws Exception {
            var taskStatus = Map.of(
                    "name", "new task",
                    "slug", "some_status"
            );

            mockMvc.perform(post("/api/task_statuses")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskStatus)))
                    .andExpect(status().isConflict());
        }
    }

    // ===== Тесты c незалогиненным пользователем (без токена) =====

    @Nested
    @DisplayName("Тесты c незалогиненным пользователем (без токена)")
    class WithOutJwtToken {
        @Test
        @DisplayName("GET/api/task_statuses - получение списка статусов без токена (Не аунтефицирован 401)")
        public void getTaskStatuses_Unauthenticated_ShouldRetorn401() throws Exception {
            mockMvc.perform(get("/api/task_statuses")
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET/api/task_statuses - получение статуса по ID без токена (Не аунтефицирован 401)")
        public void getTaskStatus_Unauthenticated_ShouldRetorn401() throws Exception {
            mockMvc.perform(get("/api/task_statuses/1")
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }
    }
}
