package hexlet.code.app.dto.userDTO;

import java.util.List;

public record IndexResponseUserData(
        List<UserDTO> userDTOList,
        Long totalUsers
) { }
