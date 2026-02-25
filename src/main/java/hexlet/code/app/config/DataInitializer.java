package hexlet.code.app.config;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.model.User.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
    private UserService userService;

    @Bean
    public CommandLineRunner initializeDatabase() {
        return (args) -> {
            String adminEmail = "dimarik70ru@gmail.com";

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
        };
    }
}
