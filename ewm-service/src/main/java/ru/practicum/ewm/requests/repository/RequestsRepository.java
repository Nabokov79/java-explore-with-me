package ru.practicum.ewm.requests.repository;

import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RequestsRepository extends JpaRepository<Request, Long> {

    Set<Request> findAllByRequesterId(Long requesterId);

    Set<Request> findByIdAndRequesterId(Long id, Long requesterId);

    Set<Request> findByIdAndEventId(Long id, Long eventId);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    Set<Request> findAllByEventId(Long eventId);

    Long countAllByEventId(long eventId);

    Long countAllByEventIdAndStatus(Long eventId, Status status);
}
