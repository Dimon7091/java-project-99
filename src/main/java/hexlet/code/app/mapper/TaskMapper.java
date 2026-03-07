package hexlet.code.app.mapper;

import hexlet.code.app.dto.taskDTO.TaskCreateDTO;
import hexlet.code.app.dto.taskDTO.TaskDTO;
import hexlet.code.app.dto.taskDTO.TaskFullUpdateDTO;
import hexlet.code.app.dto.taskDTO.TaskPartiallyUpdateDTO;
import hexlet.code.app.model.task.Task;
import hexlet.code.app.model.taskStatus.TaskStatus;
import hexlet.code.app.model.user.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract Task toEntity(TaskCreateDTO dto);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "assignee", target = "assignee_id", qualifiedByName = "assigneeToAssigneeId")
    @Mapping(source = "taskStatus", target = "status", qualifiedByName = "statusToSlug")
    public abstract TaskDTO toDto(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract void fullUpdate(TaskFullUpdateDTO dto, @MappingTarget Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract void partialUpdate(TaskPartiallyUpdateDTO dto, @MappingTarget Task model);

    @Named("statusToSlug")
    protected String statusToSlug(TaskStatus taskStatus) {
        return taskStatus.getSlug();
    }


    @Named("assigneeToAssigneeId")
    protected Long assigneeToAssigneeId(User assignee) {
        return assignee.getId();
    }
}
