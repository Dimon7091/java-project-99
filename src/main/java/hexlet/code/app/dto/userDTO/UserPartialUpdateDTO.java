package hexlet.code.app.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record UserPartialUpdateDTO(
        @Email(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "Не правильный формат email"
        )
        JsonNullable<@Email String> email,  // ← @Email внутри JsonNullable

        @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
        JsonNullable<@Size(min = 2, max = 100) String> firstName,

        @Size(min = 2, max = 100, message = "Фамилия должна быть от 2 до 100 символов")
        JsonNullable<@Size(min = 2, max = 100) String> lastName,

        @Size(min = 3, message = "Минимальная длина пароля 3 символа")
        JsonNullable<@Size(min = 3) String> password
) { }
