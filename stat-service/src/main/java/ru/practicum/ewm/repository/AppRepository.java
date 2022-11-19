package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.App;

public interface AppRepository extends JpaRepository<App, Long>, QuerydslPredicateExecutor<App> {
}
