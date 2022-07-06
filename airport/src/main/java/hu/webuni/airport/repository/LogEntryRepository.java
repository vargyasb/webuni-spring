package hu.webuni.airport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.webuni.airport.model.LogEntry;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long>{

}
