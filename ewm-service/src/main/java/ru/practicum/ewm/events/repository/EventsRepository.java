package ru.practicum.ewm.events.repository;

import ru.practicum.ewm.events.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EventsRepository extends JpaRepository<Event,Long> {

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findEventByIdAndInitiatorId(Long id, Long initiatorId);

    @Query("select e from Event e where e.initiator.id in :initiatorId " +
                                        "and e.category.id in :categoriesId ")
    List<Event> findAllEventsByInitiatorIdListAndCategoriesIdList(
                                     @Param("initiatorId") Collection<Long> initiatorId,
                                     @Param("categoriesId") Collection<Long> categoriesId
                                     );

    @Query("select e from Event e where e.category.id in :categoriesId and e.paid in :paids")
    List<Event> findAllEventsByCategoriesIdListAndPaid(@Param("categoriesId") Collection<Long> categoriesId,
                                                      @Param("paids") Collection<Boolean> paids,
                                                      Pageable pageable);
}