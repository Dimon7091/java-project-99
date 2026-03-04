package hexlet.code.app.dto.labelDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelFullUpdateDTO(
        @NotBlank
        @Size(min = 3, max = 1000)
        String name
) { }
