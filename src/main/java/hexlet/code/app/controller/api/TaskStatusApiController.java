package hexlet.code.app.controller.api;

import hexlet.code.app.dto.taskStatusDTO.TaskStatusCreateDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusFullUpdateDTO;
import hexlet.code.app.dto.taskStatusDTO.TaskStatusPartiallyUpdateDTO;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusApiController {

    @Autowired
    private TaskStatusService taskStatusService;

    @PostMapping("")
    public ResponseEntity<TaskStatusDTO> create(@Valid @RequestBody TaskStatusCreateDTO taskStatusData) {
        var savedTaskStatus = taskStatusService.create(taskStatusData);
        return ResponseEntity.created(URI.create("/api/task_statuses" + savedTaskStatus.id()))
                .body(savedTaskStatus);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskStatusDTO>> index() {

        var taskStatuses = taskStatusService.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> show(@PathVariable("id") Long id) {
        var taskStatus = taskStatusService.findById(id);
        return ResponseEntity.ok()
                .body(taskStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> fullUpdate(@PathVariable("id") Long id,
                                                    @RequestBody TaskStatusFullUpdateDTO taskStatusData) {
        var updatedTaskStatus = taskStatusService.fullUpdate(id, taskStatusData);
        return ResponseEntity.ok()
                .body(updatedTaskStatus);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> partialUpdate(@PathVariable("id") Long id,
                                                 @RequestBody TaskStatusPartiallyUpdateDTO taskStatusData) {
        var updatedTaskStatus = taskStatusService.partialUpdate(id, taskStatusData);
        return ResponseEntity.ok()
                .body(updatedTaskStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        taskStatusService.delete(id);
    }
}
