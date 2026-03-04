package hexlet.code.app.dto.taskDTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDTO(
        Long id,
        Integer index,
        Long assignee_id,
        String title,
        String content,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String status
) { }
