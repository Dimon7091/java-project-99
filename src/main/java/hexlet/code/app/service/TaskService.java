package hexlet.code.app.service;

import hexlet.code.app.dto.taskDTO.TaskCreateDTO;
import hexlet.code.app.dto.taskDTO.TaskDTO;
import hexlet.code.app.dto.taskDTO.TaskFullUpdateDTO;
import hexlet.code.app.dto.taskDTO.TaskPartiallyUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.label.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper mapper;

    // === Create ===
    public TaskDTO create(TaskCreateDTO taskData) {
        var task = mapper.toEntity(taskData);

        // Добовляем связи с лейболами
        List<Label> labels = taskData.taskLabelIds().stream()
                .map(labelId -> {
                    return labelRepository.findById(labelId)
                            .orElseThrow(() -> new ResourceNotFoundException("Метки с id: " + labelId + "не найдено"));
                })
                .toList();
        for (var label: labels) {
            task.addLabel(label);
        }
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

        // Добовляем связи с лейболами
        List<Label> labels = taskData.taskLabelIds().stream()
                .map(labelId -> {
                    return labelRepository.findById(labelId)
                            .orElseThrow(() -> new ResourceNotFoundException("Метки с id: " + labelId + "не найдено"));
                })
                .toList();
        for (var label: labels) {
            task.addLabel(label);
        }
        // Сохраняем
        var updatedTask = taskRepository.save(task);
        return mapper.toDto(updatedTask);
    }

    public TaskDTO partialUpdate(Long id, TaskPartiallyUpdateDTO taskData) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача с id: " + id + "не найдена"));
        mapper.partialUpdate(taskData, task);

        // Добовляем связи с лейболами если поле JsonNullable в запросе не null
        if (taskData.taskLabelIds().isPresent()) {
            List<Label> labels = taskData.taskLabelIds().get().stream()
                    .map(labelId -> {
                        return labelRepository.findById(labelId)
                                .orElseThrow(() -> new ResourceNotFoundException("Метки с id: " + labelId + "не найдено"));
                    })
                    .toList();
            for (var label: labels) {
                task.addLabel(label);
            }
        }
        // Сохраняем
        var updatedTask = taskRepository.save(task);
        return mapper.toDto(updatedTask);
    }

    // === Delete ===
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Задача с id: " + id + "не найдена");
        }
        taskRepository.deleteById(id);
    }

}
