package hexlet.code.app.controller.api;

import hexlet.code.app.dto.taskDTO.TaskCreateDTO;
import hexlet.code.app.dto.taskDTO.TaskDTO;
import hexlet.code.app.dto.taskDTO.TaskFullUpdateDTO;
import hexlet.code.app.dto.taskDTO.TaskPartiallyUpdateDTO;
import hexlet.code.app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    @Autowired
    private TaskService taskService;

    @PostMapping("")
    public ResponseEntity<TaskDTO> create(@Valid @RequestBody TaskCreateDTO taskData) {
        var task = taskService.create(taskData);
        return ResponseEntity.created(URI.create("/api/tasks/" + task.id()))
                .body(task);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> index() {
        var tasks = taskService.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
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
