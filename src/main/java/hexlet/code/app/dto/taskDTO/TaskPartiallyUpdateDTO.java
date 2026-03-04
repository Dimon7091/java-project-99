package hexlet.code.app.dto.taskDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public record TaskPartiallyUpdateDTO(
        @Size(min = 1, message = "Имя задачи должно быть не менее 1 символа")
        JsonNullable<@Size(min = 1) String> title,

        @Positive(message = "Индекс должен быть целым число")
        JsonNullable<@Positive Integer> index,
        JsonNullable<Long> assignee_id,
        JsonNullable<String> content,

        @NotBlank(message = "Статус не может быть пустым")
        JsonNullable<@NotBlank String> status,
        JsonNullable<List<Long>> taskLabelIds
) { }
