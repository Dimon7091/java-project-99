package hexlet.code.app.mapper;


import hexlet.code.app.dto.labelDTO.LabelCreateDTO;
import hexlet.code.app.dto.labelDTO.LabelDTO;
import hexlet.code.app.dto.labelDTO.LabelUpdateDTO;
import hexlet.code.app.model.label.Label;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class LabelMapper  {
    public abstract Label toEntity(LabelCreateDTO dto);
    public abstract LabelDTO toDto(Label model);
    public abstract void update(LabelUpdateDTO dto, @MappingTarget Label model);
}
