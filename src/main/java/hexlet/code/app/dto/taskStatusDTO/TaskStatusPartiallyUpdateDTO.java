package hexlet.code.app.dto.taskStatusDTO;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record TaskStatusPartiallyUpdateDTO(
        @Size(min = 1)
        JsonNullable<@Size(min = 1) String> name,

        @Size(min = 1)
        JsonNullable<@Size(min = 1) String> slug
) { }
