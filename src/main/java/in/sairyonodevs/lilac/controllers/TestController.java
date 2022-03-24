package in.sairyonodevs.lilac.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.sairyonodevs.lilac.models.User;
import in.sairyonodevs.lilac.repositories.UserRepository;

@RestController
@RequestMapping("/api/test")
public class TestController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}

	@GetMapping("/all-users")
	@PreAuthorize("hasRole('ADMIN')")
	public Set<User> alUsers() {
		return new HashSet<>(userRepository.findAll());
	}
}
