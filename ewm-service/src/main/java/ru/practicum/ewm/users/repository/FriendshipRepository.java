package ru.practicum.ewm.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.users.model.Friendship;
import ru.practicum.ewm.users.model.StatusUser;

import javax.transaction.Transactional;
import java.util.Set;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Set<Friendship> findAllByUserIdAndStatus(Long userId, StatusUser status);

    Friendship findByUserIdAndFriendId(Long userId, Long friendId);

    Friendship findAllByUserIdAndFriendIdAndStatus(Long userId, Long friendId, StatusUser status);

    @Modifying
    @Transactional
    @Query("delete " +
            "from Friendship f" +
            " where f.userId = ?1 and f.friendId = ?2" +
            " or f.userId = ?2 and f.friendId = ?1")
    void deleteAllByUserIdAndFriendId(Long userId, Long friendId);

    @Modifying
    @Transactional
    @Query("delete " +
            "from Friendship f" +
            " where f.userId = ?1 or f.friendId = ?1")
    void deleteAllByUserId(Long userId);
}
