package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.common.Create;
import ru.practicum.ewm.common.Update;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class UpdateEventRequest {

    @Size(groups = {Create.class}, min = 20, message = "min annotation length is 20 characters")
    @Size(groups = {Create.class}, max = 2000, message = "max annotation length is 2000 characters")
    private String annotation;
    private Long category;
    @Size(groups = {Create.class}, min = 20, message = "min description length is 20 characters")
    @Size(groups = {Create.class}, max = 7000, message = "max description length is 7000 characters")
    private String description;
    private String eventDate;
    @NotNull(groups = {Update.class}, message = "id should not null")
    private Long eventId;
    private Boolean paid;
    @PositiveOrZero(message = "participant limit not positive")
    private Integer participantLimit;
    @Size(groups = {Create.class}, min = 3, message = "min title length is 3 characters")
    @Size(groups = {Create.class},max = 120, message = "max title length is 120 characters")
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
