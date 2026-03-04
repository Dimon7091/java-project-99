package hexlet.code.app.dto.labelDTO;

import java.time.LocalDateTime;

public record LabelDTO(
        Long id,
        String name,
        LocalDateTime createdAt
) { }
