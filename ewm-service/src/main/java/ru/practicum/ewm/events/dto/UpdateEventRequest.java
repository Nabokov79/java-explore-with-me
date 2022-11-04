package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class UpdateEventRequest {

    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Long eventId;
    private Boolean paid;
    private int participantLimit;
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateEventRequest that = (UpdateEventRequest) o;
        return participantLimit == that.participantLimit && Objects.equals(annotation, that.annotation)
                                                         && Objects.equals(category, that.category)
                                                         && Objects.equals(description, that.description)
                                                         && Objects.equals(eventDate, that.eventDate)
                                                         && Objects.equals(eventId, that.eventId)
                                                         && Objects.equals(paid, that.paid)
                                                         && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, description, eventDate, eventId, paid, participantLimit, title);
    }
}
