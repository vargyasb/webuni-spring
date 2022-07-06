package hu.webuni.airport.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.Flight;
import hu.webuni.airport.repository.AirportRepository;
import hu.webuni.airport.repository.FlightRepository;

@SpringBootTest
@AutoConfigureTestDatabase
public class AirportServiceIT {

	@Autowired
	AirportService airportService;
	
	@Autowired
	AirportRepository airportRepository;
	
	@Autowired
	FlightRepository flightRepository;
	
	@BeforeEach
	public void init() {
		flightRepository.deleteAll();
		airportRepository.deleteAll();
	}
	
	@Test
	void testCreateFlight() throws Exception {
		String flightNumber = "ABC123";
		long takeoffAirport = createAirport("airport1", "iata1");
		long landingAirport = createAirport("airport2", "iata2");
		LocalDateTime takeoffDateTime = LocalDateTime.now();
		long flightId = createFlight(flightNumber, takeoffAirport, landingAirport, takeoffDateTime);
		
		Optional<Flight> savedFlightOptional = flightRepository.findById(flightId);
		assertThat(savedFlightOptional).isNotEmpty();
		Flight savedFlight = savedFlightOptional.get();
		assertThat(savedFlight.getFlightNumber()).isEqualTo(flightNumber);
		assertThat(savedFlight.getTakeOff().getId()).isEqualTo(takeoffAirport);
		assertThat(savedFlight.getLanding().getId()).isEqualTo(landingAirport);
		assertThat(savedFlight.getTakeOffTime()).isCloseTo(takeoffDateTime, within(1, ChronoUnit.MICROS));
	}
	
	@Test
	void testFindFlightByExample() throws Exception {
		long airport1Id = createAirport("airport1", "iata1");
		long airport2Id = createAirport("airport2", "iata2");
		long airport3Id = createAirport("airport3", "2iata");
		createAirport("airport4", "3ata1");
		LocalDateTime takeoffTime = LocalDateTime.of(2022, 6, 25, 11, 43);
		long flight1 = createFlight("ABC123", airport1Id, airport3Id, takeoffTime);
		long flight2 = createFlight("ABC1234", airport2Id, airport3Id, takeoffTime.plusHours(2));
		createFlight("BC123", airport1Id, airport3Id, takeoffTime);
		createFlight("ABC123", airport1Id, airport3Id, takeoffTime.plusDays(1));
		createFlight("ABC123", airport3Id, airport3Id, takeoffTime);
		
		Flight example = new Flight();
		example.setFlightNumber("ABC123");
		example.setTakeOffTime(takeoffTime);
		example.setTakeOff(new Airport("sasa", "iata"));
		List<Flight> foundFlights = this.airportService.findFlightByExample(example);
		assertThat(foundFlights.stream()
				.map(Flight::getId)
				.collect(Collectors.toList()))
				.containsExactly(flight1,flight2);
	}
	
	private long createFlight(String flightNumber, long takeoff, long landing, LocalDateTime dateTime) {
		return airportService.createFlight(flightNumber, takeoff, landing, dateTime).getId();
	}
	
	private long createAirport(String name, String iata) {
		return airportRepository.save(new Airport(name, iata)).getId();
	}
	
	
}
