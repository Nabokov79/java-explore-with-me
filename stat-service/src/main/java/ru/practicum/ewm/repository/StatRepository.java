package ru.practicum.ewm.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long>, QuerydslPredicateExecutor<Stat> {

    @Query("select e from Stat e where e.uri in :uris")
    List<Stat> findAllByUri(@Param("uris") List<String> uris);
}