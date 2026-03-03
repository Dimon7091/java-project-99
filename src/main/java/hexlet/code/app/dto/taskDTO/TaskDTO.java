package hexlet.code.app.dto.taskDTO;

import java.time.LocalDate;

public record TaskDTO(
        Long id,
        Integer index,
        LocalDate createdAt,
        Long assignee_id,
        String title,
        String content,
        String status
) { }
