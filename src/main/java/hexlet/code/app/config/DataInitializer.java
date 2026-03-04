package hexlet.code.app.config;

import hexlet.code.app.model.label.Label;
import hexlet.code.app.model.taskStatus.TaskStatus;
import hexlet.code.app.model.user.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private Faker faker;

    @Bean
    public CommandLineRunner initializeDatabase() {
        return (args) -> {
            // Если юзер репозиторий пустой создаем админа
            if (userRepository.count() == 0) {
                createUserAdmin(
                        "dimarik70rus@gmail.com",
                        "Дмитрий",
                        "Горбунов",
                        "qwerty"
                );
                // Генерируем юзеров если нужно
                generateUsers(15);
            }

            // Если репозиторий статусов пустой создаем дефолтные статусы
            if (taskStatusRepository.count() == 0) {
                createDefaultTaskStatuses();
            }

            if (labelRepository.count() == 0) {
                createDefaultLabels();
            }
        };
    }

    public void createUserAdmin(String email,
                                String firstName,
                                String lastName,
                                String password) {

        if (!userRepository.existsByEmail(email)) {
            log.info("Create default admin user: {} ", email);
            var admin = User.builder()
                    .email(email)
                    .passwordDigest(passwordEncoder.encode(password))
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();

            userRepository.save(admin);
            log.info("✅ Admin user created successfully!");
            log.info("📧 Email: {}", email);
        } else {
            log.info("Admin user is already exist: {} ", email);
        }
    }

    public void generateUsers(Integer count) {
        for (int i = 0; i < count; i++) {
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

    public void createDefaultTaskStatuses() {
        var taskStatus1 = TaskStatus.builder().name("Draft").slug("draft").build();
        var taskStatus2 = TaskStatus.builder().name("ToReview").slug("to_review").build();
        var taskStatus3 = TaskStatus.builder().name("ToBeFixed").slug("to_be_fixed").build();
        var taskStatus4 = TaskStatus.builder().name("ToPublish").slug("to_publish").build();
        var taskStatus5 = TaskStatus.builder().name("Published").slug("published").build();
        taskStatusRepository.save(taskStatus1);
        taskStatusRepository.save(taskStatus2);
        taskStatusRepository.save(taskStatus3);
        taskStatusRepository.save(taskStatus4);
        taskStatusRepository.save(taskStatus5);
    }

    public void createDefaultLabels() {
        var defaultLabel1 = Label.builder().name("feature").build();
        var defaultLabel2 = Label.builder().name("bug").build();
        labelRepository.save(defaultLabel1);
        labelRepository.save(defaultLabel2);
    }
}
