package ru.practicum.ewm.repository;

import ru.practicum.ewm.model.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("select e from Stat e where e.uri in :urisList")
    List<Stat> findAllByUri(@Param("urisList") List<String> urisList);
}