package ru.practicum.ewm.events.model;

import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.users.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToMany(mappedBy = "eventsList")
    List<Compilation> compilation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id && Objects.equals(annotation, event.annotation)
                              && Objects.equals(description, event.description)
                              && Objects.equals(eventDate, event.eventDate)
                              && Objects.equals(createdOn, event.createdOn)
                              && Objects.equals(publishedOn, event.publishedOn)
                              && Objects.equals(paid, event.paid)
                              && Objects.equals(participantLimit, event.participantLimit)
                              && Objects.equals(requestModeration, event.requestModeration)
                              && Objects.equals(title, event.title)
                              && Objects.equals(initiator, event.initiator)
                              && Objects.equals(category, event.category)
                              && Objects.equals(lat, event.lat)
                              && Objects.equals(lon, event.lon)
                              && state == event.state
                              && Objects.equals(compilation, event.compilation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, description, eventDate, createdOn, publishedOn, paid, participantLimit,
                            requestModeration, title, initiator, category, lat, lon, state, compilation);
    }
}