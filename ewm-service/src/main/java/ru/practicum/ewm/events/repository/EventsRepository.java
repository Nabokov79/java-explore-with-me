package ru.practicum.ewm.events.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.events.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventsRepository extends JpaRepository<Event,Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findEventByIdAndInitiatorId(Long id, Long initiatorId);

    Optional<Event> findByCategoryId(Long categoryId);
}