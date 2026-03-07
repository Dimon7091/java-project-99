package hexlet.code.app.service;

import hexlet.code.app.dto.labelDTO.LabelCreateDTO;
import hexlet.code.app.dto.labelDTO.LabelDTO;
import hexlet.code.app.dto.labelDTO.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceAlreadyExistsException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper mapper;

    // === Create ===
    public LabelDTO create(LabelCreateDTO labelData) {
        if (labelRepository.existsByName(labelData.name())) {
            throw new ResourceAlreadyExistsException("Метка с именем: " + labelData.name() + " уже существует");
        }
        var label = mapper.toEntity(labelData);
        var savedLabel = labelRepository.save(label);
        return mapper.toDto(savedLabel);
    }

    // === Read ===
    public List<LabelDTO> findAll() {
        var labels = labelRepository.findAll();
        return labels.stream().map(mapper::toDto).toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Метки с id: " + id + "не найдено"));
        return mapper.toDto(label);
    }

    // === Update ===
    public LabelDTO update(Long id, LabelUpdateDTO labelData) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Метки с id: " + id + "не найдено"));
        mapper.update(labelData, label);
        labelRepository.save(label);
        return mapper.toDto(label);
    }


    // === Delete ===
    public void delete(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Метки с id: " + id + "не найдено");
        }
        labelRepository.deleteById(id);
    }
}
