package ru.practicum.ewm.users.model;

import lombok.*;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "subscription", nullable = false)
    private boolean subscription;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "friendships",
            joinColumns =  {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "friend_id")})
    @WhereJoinTable(clause = "status='CONFIRMED'")
    private Set<User> friends;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns =  {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_id")})
    @WhereJoinTable(clause = "status='true'")
    private Set<User> subscribers;
}