package ru.practicum.ewm.compilations.model;

import ru.practicum.ewm.events.model.Event;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "compilations_events",
            joinColumns =  {@JoinColumn(name = "compilation_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id")})
    @ToString.Exclude
    private List<Event> eventsList;
    @Column(name = "pinned", nullable = false)
    private Boolean pinned;
    @Column(name = "title", nullable = false)
    private String title;
}
