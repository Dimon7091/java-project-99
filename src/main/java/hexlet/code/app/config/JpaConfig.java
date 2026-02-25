package hexlet.code.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing  // ← ВКЛЮЧАЕМ АУДИТ!
public class JpaConfig {

    @Bean  // ← Создаем бин, который будет давать информацию о текущем пользователе
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");  // ← всегда возвращает "system"
    }
}
