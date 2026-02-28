package hexlet.code.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Setter
@Getter

@Entity
@Table(name = "task_statuses")
@EntityListeners(AuditingEntityListener.class)
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
