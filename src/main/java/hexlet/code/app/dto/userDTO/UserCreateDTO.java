package hexlet.code.app.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(
        @Email(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "Не правильный формат email"
        )
        @NotBlank(message = "Email не может быть пустым")
        @Size(max = 100, message = "Email не должен превышать 100 символов")
        String email,

        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 100, message = "Имя не может превышать 100 символов")
        String firstName,

        @NotBlank(message = "Фамилия не может быть пустым")
        @Size(max = 100, message = "Фамилия не может превышать 100 символов")
        String lastName,

        @NotBlank
        @Size(min = 3, message = "Длина пароля не может быть менее 3 символов")
        String password
) { }
