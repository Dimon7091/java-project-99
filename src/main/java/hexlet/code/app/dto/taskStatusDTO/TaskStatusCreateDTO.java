package hexlet.code.app.dto.taskStatusDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskStatusCreateDTO(
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 1)
        String name,

        @NotBlank
        @Size(min = 1)
        String slug
) { }
