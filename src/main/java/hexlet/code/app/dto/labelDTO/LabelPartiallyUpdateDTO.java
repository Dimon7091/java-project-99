package hexlet.code.app.dto.labelDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record LabelPartiallyUpdateDTO(
        @NotBlank
        @Size(min = 3, max = 1000)
        JsonNullable<@Size(min = 3, max = 1000) String> name
) { }
