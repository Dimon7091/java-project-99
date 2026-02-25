package hexlet.code.app.dto.userDTO;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDateTime createdAt
) {}
