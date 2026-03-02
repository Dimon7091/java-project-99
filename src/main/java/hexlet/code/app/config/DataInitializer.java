package hexlet.code.app.config;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.model.taskStatus.TaskStatus;
import hexlet.code.app.model.user.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private UserService userService;

    @Bean
    public CommandLineRunner initializeDatabase() {
        return (args) -> {
            String adminEmail = "dimarik70rus@gmail.com";

            if (!userRepository.existsByEmail(adminEmail)) {
                log.info("Create default admin user: {} ", adminEmail);
                var adminData = new UserCreateDTO(
                        adminEmail,
                        "Дмитрий",
                        "Горбунов",
                        "qwerty"
                );
                userService.create(adminData);
                log.info("✅ Admin user created successfully!");
                log.info("📧 Email: {}", adminEmail);
            } else {
                log.info("Admin user is already exist: {} ", adminEmail);
            }

            generateUsers(15);
            generateDefaultTaskStatuses();
        };
    }

    public void generateUsers(Integer count) {
        for (int i = 0; i < count; i++ ) {
            User user = User.builder()
                    .email(faker.internet().emailAddress())
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .passwordDigest(faker.lorem().characters(6))
                    .build();
            if (!userRepository.existsByEmail(user.getEmail())) {
                userRepository.save(user);
                log.info("✅ user with email: {} created", user.getEmail());
            } else {
                log.info("❌ user with email: {} is already exist", user.getEmail());
            }
        }
    }

    public void generateDefaultTaskStatuses() {
        var task1 = TaskStatus.builder().name("Draft").slug("draft").build();
        var task2 = TaskStatus.builder().name("ToReview").slug("to_review").build();
        var task3 = TaskStatus.builder().name("ToBeFixed").slug("to_be_fixed").build();
        var task4 = TaskStatus.builder().name("ToPublish").slug("to_publish").build();
        var task5 = TaskStatus.builder().name("Published").slug("published").build();
        taskStatusRepository.save(task1);
        taskStatusRepository.save(task2);
        taskStatusRepository.save(task3);
        taskStatusRepository.save(task4);
        taskStatusRepository.save(task5);
    }
}
