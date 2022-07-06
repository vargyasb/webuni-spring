package hu.webuni.airport.service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.webuni.airport.model.AirportUser;
import hu.webuni.airport.repository.UserRepository;

@Service
public class InitDbService {

	UserRepository userRepository;
	PasswordEncoder passwordEncoder;
	
	public InitDbService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public void createUsersIfNeeded() {
		
		if (!userRepository.existsById("admin")) {
			userRepository.save(new AirportUser("admin", passwordEncoder.encode("pass"), Set.of("admin","user")));
		}
		if (!userRepository.existsById("user")) {
			userRepository.save(new AirportUser("user", passwordEncoder.encode("pass"), Set.of("user")));
		}
	}
}
