package ru.practicum.ewm.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.users.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @Column(name = "annotation", length = 2000)
    private String annotation;

    @Column(name = "description", length = 7000)
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
    private boolean requestModeration;

    @Column(name = "title", length = 120)
    private String title;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id",  nullable = false)
    private User initiator;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",  nullable = false)
    private Category category;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    private Set<Compilation> compilation;

    @OneToMany(mappedBy = "event",
            orphanRemoval = true,
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Request> requests = new HashSet<>();

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