package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.common.Create;
import ru.practicum.ewm.common.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class UpdateEventRequest {

    @NotBlank(groups = {Update.class}, message = "annotation date should not be blank")
    private String annotation;
    private Long category;

    @NotBlank(groups = {Update.class}, message = "description date should not be blank")
    private String description;
    private String eventDate;
    @NotNull(groups = {Update.class}, message = "id should not null")
    private Long eventId;
    private boolean paid;
    @PositiveOrZero(message = "participant limit not positive")
    private Integer participantLimit;

    @NotBlank(groups = {Create.class}, message = "title date should not be blank")
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateEventRequest that = (UpdateEventRequest) o;
        return paid == that.paid && Objects.equals(annotation, that.annotation)
                                 && Objects.equals(category, that.category)
                                 && Objects.equals(description, that.description)
                                 && Objects.equals(eventDate, that.eventDate)
                                 && Objects.equals(eventId, that.eventId)
                                 && Objects.equals(participantLimit, that.participantLimit)
                                 && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, description, eventDate, eventId, paid, participantLimit, title);
    }
}
