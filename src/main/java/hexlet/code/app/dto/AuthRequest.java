package hexlet.code.app.dto;

public record AuthRequest(
        String username,
        String password
) { }
