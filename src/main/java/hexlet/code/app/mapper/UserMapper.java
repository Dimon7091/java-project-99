package hexlet.code.app.mapper;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.dto.userDTO.UserDTO;
import hexlet.code.app.dto.userDTO.UserFullUpdateDTO;
import hexlet.code.app.dto.userDTO.UserPartialUpdateDTO;
import hexlet.code.app.model.User.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class UserMapper  {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(source = "password", target = "passwordDigest", qualifiedByName = "hashPassword")
    public abstract User toEntity(UserCreateDTO dto);
    public abstract UserDTO toDto(User model);
    public abstract void fullUpdate(UserFullUpdateDTO dto, @MappingTarget User model);
    public abstract void partialUpdate(UserPartialUpdateDTO dto, @MappingTarget User model);

    @Named("hashPassword")
    protected String hashPassword(String rawPassword) {
        return rawPassword == null ? null : passwordEncoder.encode(rawPassword);
    }
}
