package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

public interface RequestsRepository extends JpaRepository<Request, Long> {

    Set<Request> findAllByRequesterId(Long requesterId);

    Request findByIdAndRequesterId(Long id, Long requesterId);

    @Query("select r from Request r" +
            " left join Event e on e.id = r.event.id " +
            "where e.initiator.id = ?1 and r.event.id = ?2")
    Set<Request> findByInitiatorIdAndRequesterId(Long initiatorId, Long eventId);

    Set<Request> findByIdAndEventId(Long id, Long eventId);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);


    Set<Request> findAllByEventId(Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, Status status);
}
