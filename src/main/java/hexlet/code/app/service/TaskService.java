package hexlet.code.app.service;

import hexlet.code.app.dto.taskDTO.TaskCreateDTO;
import hexlet.code.app.dto.taskDTO.TaskDTO;
import hexlet.code.app.dto.taskDTO.TaskFullUpdateDTO;
import hexlet.code.app.dto.taskDTO.TaskPartiallyUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.label.Label;
import hexlet.code.app.model.task.Task;
import hexlet.code.app.model.taskStatus.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    // === Create ===
    public TaskDTO create(TaskCreateDTO taskData) {
        var task = mapper.toEntity(taskData);

        // Добовляем связи
        addContactWithLabels(task, taskData.taskLabelIds());
        addContactWithUsers(task, taskData.assignee_id());

        var status = slugToStatus(taskData.status());
        task.setTaskStatus(status);

        // Сохраняем
        var savedTask = taskRepository.save(task);
        return mapper.toDto(savedTask);
    }

    // === Read ===
    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id: " + id + "не найдена"));
        return mapper.toDto(task);
    }

    public List<TaskDTO> findAll() {
        var tasks = taskRepository.findAll();
        return tasks.stream().map(mapper::toDto).toList();
    }

    // === Update ===
    public TaskDTO fullUpdate(Long id, TaskFullUpdateDTO taskData) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id: " + id + "не найдена"));
        mapper.fullUpdate(taskData, task);

        // Добовляем связи
        addContactWithLabels(task, taskData.taskLabelIds());
        addContactWithUsers(task, taskData.assignee_id());

        var status = slugToStatus(taskData.status());
        task.setTaskStatus(status);

        // Сохраняем
        var updatedTask = taskRepository.save(task);
        return mapper.toDto(updatedTask);
    }

    public TaskDTO partialUpdate(Long id, TaskPartiallyUpdateDTO taskData) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id: " + id + "не найдена"));
        mapper.partialUpdate(taskData, task);

        // Добовляем связи если поля переданны в запросе
        if (taskData.taskLabelIds().isPresent()) {
            addContactWithLabels(task, taskData.taskLabelIds().get());
        }

        if (taskData.assignee_id().isPresent()) {
            addContactWithUsers(task, taskData.assignee_id().get());
        }

        if (taskData.status().isPresent()) {
            var status = slugToStatus(taskData.status().get());
            task.setTaskStatus(status);
        }

        // Сохраняем
        var updatedTask = taskRepository.save(task);
        return mapper.toDto(updatedTask);
    }

    // === Delete ===
    public void delete(Long id) {
        var task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Задача с id: " + id + "не найдена"));
        // Удаляем у связанного пользователя задачу
        var user = task.getAssignee();
        user.removeTask(task);

        taskRepository.deleteById(id);
    }

    // Вспомогательные методы
    public void addContactWithLabels(Task task, List<Long> labelsId) {
        List<Label> labels = labelsId.stream()
                .map(labelId -> {
                    return labelRepository.findById(labelId)
                            .orElseThrow(() -> new ResourceNotFoundException("Метки с id: " + labelId + "не найдено"));
                })
                .toList();
        for (var label: labels) {
            task.addLabel(label);
        }
    }

    public void addContactWithUsers(Task task, Long assigneeId) {
        var assignee = userRepository.findById(assigneeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пользователь с id " + assigneeId + " не существует"));
        task.addAssignee(assignee);
    }

    public TaskStatus slugToStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Статус со слагом: " + slug + " не найден"));
    }
}
