package ru.practicum.ewm.events.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.events.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventsRepository extends JpaRepository<Event,Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findEventByIdAndInitiatorId(Long id, Long initiatorId);

    @Query("select e from Event e where e.initiator.id in :initiatorId " +
                                        "and e.category.id in :categoriesId ")
    List<Event> findAllEventsByInitiatorIdListAndCategoriesIdList(
                                     @Param("initiatorId") Collection<Long> initiatorId,
                                     @Param("categoriesId") Collection<Long> categoriesId
                                     );

    Optional<Event> findByCategoryId(Long categoryId);

}