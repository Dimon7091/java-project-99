package hexlet.code.app.dto.taskDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TaskFullUpdateDTO(
        @NotBlank(message = "Имя задачи не может быть пустым")
        @Size(min = 1, message = "Имя задачи должно быть не менее 1 символа")
        String title,

        @Positive(message = "Индекс должен быть целым число")
        Integer index,
        Long assignee_id,
        String content,

        @NotBlank(message = "Статус не может быть пустым")
        String status,
        List<Long> taskLabelIds
) { }
