package br.com.teste.fullstackapi.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.teste.fullstackapi.exception.EmailAlreadyExistsException;
import br.com.teste.fullstackapi.model.Role;
import br.com.teste.fullstackapi.model.User;
import br.com.teste.fullstackapi.repository.UserRepository;
import br.com.teste.fullstackapi.security.dto.AuthenticationRequest;
import br.com.teste.fullstackapi.security.dto.AuthenticationResponse;
import br.com.teste.fullstackapi.security.dto.RegisterRequest;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("O e-mail informado já está em uso.");
        }


        User user = new User();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setSenha(passwordEncoder.encode(request.getSenha()));
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);

        savedUser.setUserIdCreated(savedUser.getId());
        savedUser.setUserIdUpdated(savedUser.getId());
        savedUser.setFrontendOriginCreated(request.getFrontendOrigin());
        savedUser.setFrontendOriginUpdated(request.getFrontendOrigin());
        
        userRepository.save(savedUser);

        var jwtToken = jwtService.generateToken(savedUser);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse registerAdmin(RegisterRequest request) {

        User adminUser = new User();
        adminUser.setNome(request.getNome());
        adminUser.setEmail(request.getEmail());
        adminUser.setSenha(passwordEncoder.encode(request.getSenha()));
        adminUser.setRole(Role.ROLE_ADMIN); 

        User savedUser = userRepository.save(adminUser);

        savedUser.setUserIdCreated(savedUser.getId());
        savedUser.setUserIdUpdated(savedUser.getId());
        savedUser.setFrontendOriginCreated(request.getFrontendOrigin());
        savedUser.setFrontendOriginUpdated(request.getFrontendOrigin());
        
        userRepository.save(savedUser);

        var jwtToken = jwtService.generateToken(savedUser);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}