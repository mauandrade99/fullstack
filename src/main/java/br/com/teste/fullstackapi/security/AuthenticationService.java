package br.com.teste.fullstackapi.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.teste.fullstackapi.model.Role;
import br.com.teste.fullstackapi.model.User;
import br.com.teste.fullstackapi.repository.UserRepository;
import br.com.teste.fullstackapi.security.dto.AuthenticationRequest;
import br.com.teste.fullstackapi.security.dto.AuthenticationResponse;
import br.com.teste.fullstackapi.security.dto.RegisterRequest;

import br.com.teste.fullstackapi.exception.EmailAlreadyExistsException;

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
            // Se o Optional não estiver vazio, significa que o e-mail já existe.
            // Lançamos nossa própria exceção customizada.
            throw new EmailAlreadyExistsException("O e-mail informado já está em uso.");
        }


        User user = new User();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setSenha(passwordEncoder.encode(request.getSenha()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse registerAdmin(RegisterRequest request) {

        User adminUser = new User();
        adminUser.setNome(request.getNome());
        adminUser.setEmail(request.getEmail());
        adminUser.setSenha(passwordEncoder.encode(request.getSenha()));
        adminUser.setRole(Role.ROLE_ADMIN); 
        
        userRepository.save(adminUser);

        var jwtToken = jwtService.generateToken(adminUser);
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