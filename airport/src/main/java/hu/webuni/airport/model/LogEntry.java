package hu.webuni.airport.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class LogEntry {

	@Id
	@GeneratedValue
	private long id;
	
	private LocalDateTime ts;
	private String description;
	private String username;
	
	public LogEntry() {
		
	}
	
	public LogEntry(String description, String username) {
		this.description = description;
		this.username = username;
		this.ts = LocalDateTime.now();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDateTime getTs() {
		return ts;
	}

	public void setTs(LocalDateTime ts) {
		this.ts = ts;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
