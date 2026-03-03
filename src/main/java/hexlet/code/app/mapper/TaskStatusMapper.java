package hexlet.code.app.mapper;

import hexlet.code.app.dto.taskStatusDTO.TaskStatusCreateDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusFullUpdateDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusPartiallyUpdateDTO;
import hexlet.code.app.model.taskStatus.TaskStatus;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;


@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskStatusMapper  {
    public abstract TaskStatus toEntity(TaskStatusCreateDTO dto);
    public abstract TaskStatusDTO toDto(TaskStatus model);
    public abstract void fullUpdate(TaskStatusFullUpdateDTO dto, @MappingTarget TaskStatus model);
    public abstract void partialUpdate(TaskStatusPartiallyUpdateDTO dto, @MappingTarget TaskStatus model);
}
