package hexlet.code.app.dto.userDTO;

public record AuthRequest(
        String username,
        String password
) { }
