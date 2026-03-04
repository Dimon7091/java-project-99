package hexlet.code.app.controller.api;

import hexlet.code.app.dto.labelDTO.LabelCreateDTO;
import hexlet.code.app.dto.labelDTO.LabelDTO;
import hexlet.code.app.dto.labelDTO.LabelUpdateDTO;
import hexlet.code.app.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelApiController {

    @Autowired
    private LabelService labelService;

    @PostMapping("")
    public ResponseEntity<LabelDTO> create(@RequestBody LabelCreateDTO labelData) {
        var label = labelService.create(labelData);
        return ResponseEntity.created(URI.create("/api/labels/" + label.id()))
                .body(label);
    }

    @GetMapping("")
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelService.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> show(@PathVariable Long id) {
        var label = labelService.findById(id);
        return ResponseEntity.ok()
                .body(label);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> update(@PathVariable Long id, @RequestBody LabelUpdateDTO labelData) {
        var updatedLabel = labelService.update(id, labelData);
        return ResponseEntity.ok()
                .body(updatedLabel);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}
