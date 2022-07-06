package hu.webuni.airport.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.Flight;
import hu.webuni.airport.repository.AirportRepository;
import hu.webuni.airport.repository.FlightRepository;

@Service
public class AirportService {
	
	AirportRepository airportRepository;
	FlightRepository flightRepository;
	LogEntryService logEntryService;
	
	public AirportService(AirportRepository airportRepository, FlightRepository flightRepository, LogEntryService logEntryService) {
		this.airportRepository = airportRepository;
		this.flightRepository = flightRepository;
		this.logEntryService = logEntryService;
	}

//	@PersistenceContext
//	EntityManager em;

	@Transactional
	public Airport save(Airport airport) {
		checkUniqueIata(airport.getIata(), null);
//		em.persist(airport);
		return airportRepository.save(airport);
	}
	
	@Transactional
	public Airport update(Airport airport) {
		checkUniqueIata(airport.getIata(), airport.getId());
		if (airportRepository.existsById(airport.getId())) {
			logEntryService.createLog(String.format("Airport modified with id %d new name is %s", 
					airport.getId(), airport.getName()));
			return airportRepository.save(airport);			
		} else {
			throw new NoSuchElementException();
		}
	}

	private void checkUniqueIata(String iata, Long id) {
		boolean forUpdate = id != null;
//		TypedQuery<Long> query = em.createNamedQuery(forUpdate ? "Airport.countByIataAndNotIn" : "Airport.countByIata", Long.class)
//				.setParameter("iata", iata);
//		if(forUpdate)
//			query.setParameter("id", id);
//		
//		Long count = query
//				.getSingleResult();
		
		Long count = forUpdate ? 
				airportRepository.countByIataAndIdNot(iata, id)
				: airportRepository.countByIata(iata);
		
		if (count > 0) {
			throw new NonUniqueIataException(iata);
		}
	}

	public List<Airport> findAll() {
//		return em.createQuery("SELECT a FROM Airport a", Airport.class).getResultList();
		return airportRepository.findAll();
	}

	public Optional<Airport> findById(long id) {
//		return em.find(Airport.class, id);
		return airportRepository.findById(id);
	}

	@Transactional
	public void delete(long id) {
//		em.remove(findById(id));
		airportRepository.deleteById(id);
	}
	
	@Transactional
	public Flight createFlight(String flightNumber, long takeoffAirportId, long landingAirportId, LocalDateTime takeoffDateTime) {
		Flight flight = new Flight();
		flight.setFlightNumber(flightNumber);
		flight.setTakeOff(airportRepository.findById(takeoffAirportId).get());
		flight.setLanding(airportRepository.findById(landingAirportId).get());
		flight.setTakeOffTime(takeoffDateTime);
		return flightRepository.save(flight);
	}
	
	public List<Flight> findFlightByExample(Flight example) {
		long id = example.getId();
		String flightNumber = example.getFlightNumber();
		
		String takeoffIata = null;
		Airport takeoff = example.getTakeOff();
		if(takeoff != null)
			takeoffIata = takeoff.getIata();
		
		LocalDateTime takeoffTime = example.getTakeOffTime();
		
		Specification<Flight> spec = Specification.where(null);
		
		if(id > 0)
			spec = spec.and(FlightSpecifications.hasId(id));
		if(StringUtils.hasText(flightNumber))
			spec = spec.and(FlightSpecifications.hasFlightNumber(flightNumber));
		if(StringUtils.hasText(takeoffIata))
			spec = spec.and(FlightSpecifications.hasTakeoffIata(takeoffIata));
		if(takeoffTime != null)
			spec = spec.and(FlightSpecifications.hasTakeoffTime(takeoffTime));
			
		return flightRepository.findAll(spec, Sort.by("id"));
	}

}
