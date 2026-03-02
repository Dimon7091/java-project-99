package hexlet.code.app.dto.taskStatusDTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TaskStatusDTO(
        Long id,
        String name,
        String slug,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) { }
