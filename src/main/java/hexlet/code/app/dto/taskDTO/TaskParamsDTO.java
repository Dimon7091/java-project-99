package hexlet.code.app.dto.taskDTO;

public record TaskParamsDTO(
        String titleCont,
        Long assigneeId,
        String status,
        Long labelId
) { }
