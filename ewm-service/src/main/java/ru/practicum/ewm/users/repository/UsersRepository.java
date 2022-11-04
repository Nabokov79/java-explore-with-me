package ru.practicum.ewm.users.repository;

import ru.practicum.ewm.users.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.id in :idList")
    List<User> findAllById(@Param("idList") List<Long> idList, Pageable pageable);
}
