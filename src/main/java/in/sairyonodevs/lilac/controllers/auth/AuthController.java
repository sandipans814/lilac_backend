package in.sairyonodevs.lilac.controllers.auth;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.sairyonodevs.lilac.models.User;
import in.sairyonodevs.lilac.models.request.LoginRequest;
import in.sairyonodevs.lilac.models.request.SignupRequest;
import in.sairyonodevs.lilac.models.response.JwtResponse;
import in.sairyonodevs.lilac.models.response.MessageResponse;
import in.sairyonodevs.lilac.services.MyUserDetails;
import in.sairyonodevs.lilac.services.UserService;

import in.sairyonodevs.lilac.utils.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	// @Autowired
	// UserRepository userRepository;

	// @Autowired
	// RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(
            jwt, 
            userDetails.getId(), 
            userDetails.getEmail(), 
            roles
        ));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userService.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		if (!(signUpRequest.getGender().equals("M") || signUpRequest.getGender().equals("W"))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Invalid argument for gender!"));
		}

		// Create new user's account
		User user = new User(
            signUpRequest.getFirstName(),
            signUpRequest.getLastName(),
            signUpRequest.getEmail(),
			encoder.encode(signUpRequest.getPassword()),
            "", // phoneNo
            "",  // address
			signUpRequest.getGender()
        );

		Set<String> strRoles = signUpRequest.getRoles();

		userService.register(user, strRoles);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}

