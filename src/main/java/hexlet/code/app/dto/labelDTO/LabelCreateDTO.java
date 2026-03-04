package hexlet.code.app.dto.labelDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelCreateDTO(
        @NotBlank
        @Size(min = 3, max = 1000, message = "Имя метки должно быть от 3х до 1000 символов")
        String name
) { }
