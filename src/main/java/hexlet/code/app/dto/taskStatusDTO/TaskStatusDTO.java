package hexlet.code.app.dto.taskStatusDTO;

import java.time.LocalDateTime;

public record TaskStatusDTO(
        Long id,
        String name,
        String slug,
        LocalDateTime createdAt
) { }
