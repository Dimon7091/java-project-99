package hexlet.code.app.dto.userDTO;

import java.util.List;

public record IndexResponseData(
        List<UserDTO> userDTOList,
        Long totalUsers
) { }
