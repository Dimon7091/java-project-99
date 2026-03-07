package hexlet.code.app.specification;

import hexlet.code.app.dto.taskDTO.TaskParamsDTO;
import hexlet.code.app.model.label.Label;
import hexlet.code.app.model.task.Task;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.titleCont())
                .and(withAssigneeId(params.assigneeId()))
                .and(withStatus(params.status()))
                .and(withLabelId(params.labelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) ->
                titleCont == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), titleCont);
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? cb.conjunction() : cb.equal((root.get("assignee").get("id")), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.like(cb.lower(root.get("taskStatus").get("slug")), status);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction(); // ничего не фильтруем
            }

            // Присоединяем таблицу меток
            Join<Task, Label> labelsJoin = root.join("labels");

            // Сравниваем ID метки
            return cb.equal(labelsJoin.get("id"), labelId);
        };
    }
}
