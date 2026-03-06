package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.task.Task;
import hexlet.code.app.model.taskStatus.TaskStatus;
import hexlet.code.app.model.user.User;
import hexlet.code.app.repository.TaskRepository;
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

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"dev", "test"})
public class TaskApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;
    private String userToken;
    private User testUser;

    private TaskStatus testStatus;
    private TaskStatus anotherStatus;

    private Task testTask;
    private Task anotherTask;

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
                .name("Test status")
                .slug("test_status")
                .build();
        anotherStatus = TaskStatus.builder()
                .name("another status")
                .slug("another_status")
                .build();
        taskStatusRepository.save(testStatus);
        taskStatusRepository.save(anotherStatus);

        // Создаем тестовые задачи
        testTask = Task.builder()
                .name("Task")
                .index(12)
                .description("Tesk task")
                .taskStatus(testStatus)
                .assignee(testUser)
                .build();
        anotherTask = Task.builder()
                .name("Task")
                .index(12)
                .description("Tesk task")
                .taskStatus(testStatus)
                .assignee(testUser)
                .build();
        taskRepository.save(testTask);
        taskRepository.save(anotherTask);
    }

    // ===== ТЕСТЫ =====

    @Nested
    @DisplayName("Тесты c залогиненным пользователем (с токеном)")
    class WithJwtToken {
        @Test
        @DisplayName("POST/api/tasks - успешное создание задачи (аунтефицирован 201)")
        public void postTask_Authenticated_ShouldRetorn201() throws Exception {
            var taskData = Map.of(
                    "title", "Task for test",
                    "index", 13,
                    "content", "Test task for test",
                    "status", "test_status",
                    "assignee_id", testUser.getId(),
                    "taskLabelIds", List.of(1)
            );

            mockMvc.perform(post("/api/tasks")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskData)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value(taskData.get("title")))
                    .andExpect(jsonPath("$.index").value(taskData.get("index")))
                    .andExpect(jsonPath("$.content").value(taskData.get("content")))
                    .andExpect(jsonPath("$.status").value(taskData.get("status")))
                    .andExpect(jsonPath("$.assignee_id").value(taskData.get("assignee_id")));
        }

        @Test
        @DisplayName("GET/api/tasks - успешное получение списка задачи (аунтефицирован 200)")
        public void getTasks_Authenticated_ShouldRetorn200() throws Exception {
            mockMvc.perform(get("/api/tasks")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET/api/tasks/id - успешное получение задачи по ID (аунтефицирован 200)")
        public void getTaskById_Authenticated_ShouldRetorn200() throws Exception {
            mockMvc.perform(get("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testTask.getId()))
                    .andExpect(jsonPath("$.title").value(testTask.getName()))
                    .andExpect(jsonPath("$.index").value(testTask.getIndex()))
                    .andExpect(jsonPath("$.content").value(testTask.getDescription()))
                    .andExpect(jsonPath("$.status").value(testTask.getTaskStatus().getSlug()))
                    .andExpect(jsonPath("$.assignee_id").value(testTask.getAssignee().getId()));
        }

        @Test
        @DisplayName("PUT/api/tasks/id - успешное полное обновлени задачи (аунтефицирован 200)")
        public void fullUpdateTask_Authenticated_ShouldRetorn200() throws Exception {
            var updatedTaskData = Map.of(
                    "title", "Updated task",
                    "index", 14,
                    "content", "Test task for test update",
                    "status", anotherStatus.getSlug(),
                    "assignee_id", testUser.getId(),
                    "taskLabelIds", List.of(1)
            );

            mockMvc.perform(put("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskData)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(updatedTaskData.get("title")))
                    .andExpect(jsonPath("$.index").value(updatedTaskData.get("index")))
                    .andExpect(jsonPath("$.content").value(updatedTaskData.get("content")))
                    .andExpect(jsonPath("$.status").value(updatedTaskData.get("status")))
                    .andExpect(jsonPath("$.assignee_id").value(updatedTaskData.get("assignee_id")));
        }

        @Test
        @DisplayName("PATCH/api/tasks/id - успешное частичное обновлени задачи (аунтефицирован 200)")
        public void partialUpdateTask_Authenticated_ShouldRetorn200() throws Exception {
            var updatedTaskData = Map.of(
                    "title", "Updated task",
                    "content", "Test task for test update"
            );

            mockMvc.perform(patch("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskData)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(updatedTaskData.get("title")))
                    .andExpect(jsonPath("$.content").value(updatedTaskData.get("content")));
        }

        @Test
        @DisplayName("DELETE/api/tasks - успешное удаление задачи (аунтефицирован 204)")
        public void deleteTask_Authenticated_ShouldRetorn204() throws Exception {
            mockMvc.perform(delete("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNoContent());
        }

        // ===== Тесты с невалидными данными =====

        @Test
        @DisplayName("POST/api/tasks - создание задачи с невалидным именем (аунтефицирован 422)")
        public void postTasksInvalidName_Authenticated_ShouldRetorn422() throws Exception {
            var taskData = Map.of(
                    "title", "",
                    "index", 13,
                    "content", "Test task for test",
                    "status", testStatus.getSlug(),
                    "assignee_id", testUser.getId()
            );

            mockMvc.perform(post("/api/tasks")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskData)))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("POST/api/tasks - создание задачи с без статуса (аунтефицирован 422)")
        public void postTasksWithOutStatus_Authenticated_ShouldRetorn422() throws Exception {
            var taskData = Map.of(
                    "title", "Task",
                    "index", 13,
                    "content", "Test task for test",
                    "status", "",
                    "assignee_id", testUser.getId()
            );

            mockMvc.perform(post("/api/tasks")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskData)))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("POST/api/tasks - создание задачи с несуществующим статусом (аунтефицирован 404)")
        public void postTasksWithNonExistentStatus_Authenticated_ShouldRetorn422() throws Exception {
            var taskData = Map.of(
                    "title", "Task",
                    "index", 13,
                    "content", "Test task for test",
                    "status", "non-existent-status",
                    "assignee_id", testUser.getId()
            );

            mockMvc.perform(post("/api/tasks")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskData)))
                    .andExpect(status().isNotFound());
        }
    }

    // ===== Тесты c незалогиненным пользователем (без токена) =====

    @Nested
    @DisplayName("Тесты c незалогиненным пользователем (без токена)")
    class WithOutJwtToken {

        @Test
        @DisplayName("POST/api/tasks - ну успешное создание задачи (не аунтефицирован 401)")
        public void postTask_Unauthenticated_ShouldRetorn201() throws Exception {
            var taskData = Map.of(
                    "title", "Task for test",
                    "index", 13,
                    "content", "Test task for test",
                    "status", "test_status",
                    "assignee_id", testUser.getId()
            );

            mockMvc.perform(post("/api/tasks")
                            .header("Authorization", "Bearer ")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskData)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET/api/tasks - не успешное получение списка задач (не аунтефицирован 401)")
        public void getTasks_Unauthenticated_ShouldRetorn200() throws Exception {
            mockMvc.perform(get("/api/tasks")
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET/api/tasks/id - не успешное получение задачи по ID (не аунтефицирован 401)")
        public void getTaskById_Unauthenticated_ShouldRetorn200() throws Exception {
            mockMvc.perform(get("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT/api/tasks/id - не успешное полное обновлени задачи (не аунтефицирован 401)")
        public void fullUpdateTask_Unauthenticated_ShouldRetorn200() throws Exception {
            var updatedTaskData = Map.of(
                    "title", "Updated task",
                    "index", 14,
                    "content", "Test task for test update",
                    "status", anotherStatus.getSlug(),
                    "assignee_id", testUser.getId()
            );

            mockMvc.perform(put("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer ")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskData)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PATCH/api/tasks/id - не успешное частичное обновлени задачи (не аунтефицирован 401)")
        public void partialUpdateTask_Unauthenticated_ShouldRetorn200() throws Exception {
            var updatedTaskData = Map.of(
                    "title", "Updated task",
                    "content", "Test task for test update"
            );

            mockMvc.perform(patch("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer ")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskData)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE/api/tasks - не успешное удаление задачи (не аунтефицирован 401)")
        public void deleteTask_Unauthenticated_ShouldRetorn204() throws Exception {
            mockMvc.perform(delete("/api/tasks/" + testTask.getId())
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }


    }
}
