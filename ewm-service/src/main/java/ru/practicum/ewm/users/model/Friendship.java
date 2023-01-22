package ru.practicum.ewm.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "friend_id")
    private Long friendId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusUser status;
}
