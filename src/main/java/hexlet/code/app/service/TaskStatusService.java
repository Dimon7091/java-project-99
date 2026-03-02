package hexlet.code.app.service;

import hexlet.code.app.dto.taskStatusDTO.TaskStatusCreateDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusFullUpdateDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusPartiallyUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.exception.StatusSlugAlreadyExistsException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper mapper;

    // === Create ===
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        if (taskStatusRepository.existsBySlug(taskStatusData.slug())) {
            throw new StatusSlugAlreadyExistsException("Слаг " + taskStatusData.slug() + " уже существует");
        }
        var taskStatus = mapper.toEntity(taskStatusData);
        var savedTaskStatus = taskStatusRepository.save(taskStatus);
        return mapper.toDto(savedTaskStatus);
    }

    // === Read ===
    public List<TaskStatusDTO> findAll() {
        var taskStatuses =  taskStatusRepository.findAll();
        return taskStatuses.stream().map(mapper::toDto).toList();
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статус с id " + id + " не существует"));
        return mapper.toDto(taskStatus);
    }

    public TaskStatusDTO findBySlug(String slug) {
        var taskStatus = taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Статус со слагом: " + slug + " не существует"));
        return mapper.toDto(taskStatus);
    }

    // === Update ===
    public TaskStatusDTO fullUpdate(Long id, TaskStatusFullUpdateDTO taskStatusData) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статуса с id " + id + " не существует"));

        if (!taskStatus.getSlug().equals(taskStatusData.slug())
                && taskStatusRepository.existsBySlug(taskStatusData.slug())) {
            throw new StatusSlugAlreadyExistsException("Слаг " + taskStatusData.slug() + " уже занят");
        }

        mapper.fullUpdate(taskStatusData, taskStatus);
        taskStatusRepository.save(taskStatus);
        return mapper.toDto(taskStatus);
    }

    public TaskStatusDTO partialUpdate(Long id, TaskStatusPartiallyUpdateDTO taskStatusData) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статуса с id " + id + " не существует"));

        if (taskStatusData.slug() != null && taskStatusData.slug().isPresent()) {
            String newSlug = taskStatusData.slug().get();

            if (!taskStatus.getSlug().equals(newSlug)
                    && taskStatusRepository.existsBySlug(newSlug)) {
                throw new StatusSlugAlreadyExistsException("Слаг " + newSlug + " уже занят");
            }
        }

        mapper.partialUpdate(taskStatusData, taskStatus);
        taskStatusRepository.save(taskStatus);
        return mapper.toDto(taskStatus);
    }

    // === Delete ===
    public void delete(Long id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Задачи с id " + id + " не существует");
        }
        taskStatusRepository.deleteById(id);
    }
}
