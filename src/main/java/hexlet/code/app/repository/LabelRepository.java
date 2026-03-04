package hexlet.code.app.repository;

import hexlet.code.app.model.label.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByName(String name);
    Boolean existsByName(String name);
}
