package ru.practicum.dto.requests;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    @NotBlank(message = "Отсутствует новый статус заявок")
    private String status;
    @NotNull(message = "Отсутствует список заявок")
    private List<Long> requestIds;
}
