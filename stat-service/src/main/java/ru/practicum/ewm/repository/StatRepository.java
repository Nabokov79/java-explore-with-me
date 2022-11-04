package ru.practicum.ewm.repository;

import ru.practicum.ewm.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select e from EndpointHit e where e.uri in :urisList")
    List<EndpointHit> findAllByUri(@Param("urisList") List<String> urisList);
}