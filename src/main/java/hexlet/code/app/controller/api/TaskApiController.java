package hexlet.code.app.controller.api;

import hexlet.code.app.dto.taskDTO.TaskDTO;
import hexlet.code.app.dto.taskDTO.TaskParamsDTO;
import hexlet.code.app.dto.taskDTO.TaskCreateDTO;
import hexlet.code.app.dto.taskDTO.TaskPartiallyUpdateDTO;
import hexlet.code.app.dto.taskDTO.TaskFullUpdateDTO;
import hexlet.code.app.service.TaskService;
import hexlet.code.app.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskSpecification specBuilder;

    @PostMapping("")
    public ResponseEntity<TaskDTO> create(@Valid @RequestBody TaskCreateDTO taskData) {
        var task = taskService.create(taskData);
        return ResponseEntity.created(URI.create("/api/tasks/" + task.id()))
                .body(task);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> index(
            TaskParamsDTO sortParams,
            @RequestParam(name = "_start", defaultValue = "0") int start,
            @RequestParam(name = "_end", defaultValue = "10") int end,
            @RequestParam(name = "_sort", defaultValue = "id") String sort,
            @RequestParam(name = "_order", defaultValue = "ASC") String order) {
        // Пагинация и фильтрация
        int page = start / (end - start);
        int size = end - start;
        Sort.Direction direction = order.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        var spec = specBuilder.build(sortParams);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        var tasksPage = taskService.findAll(pageable, spec);
        var response = tasksPage.stream().toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasksPage.getTotalElements()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> show(@PathVariable("id") Long id) {
        var task = taskService.findById(id);
        return ResponseEntity.ok()
                .body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> fullUpdate(@PathVariable("id") Long id,
                                              @Valid @RequestBody TaskFullUpdateDTO taskData) {
        var updatedTask = taskService.fullUpdate(id, taskData);
        return ResponseEntity.ok()
                .body(updatedTask);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> partialUpdate(@PathVariable("id") Long id,
                                                 @Valid @RequestBody TaskPartiallyUpdateDTO taskData) {
        var updatedTask = taskService.partialUpdate(id, taskData);
        return ResponseEntity.ok()
                .body(updatedTask);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        taskService.delete(id);
    }
}
