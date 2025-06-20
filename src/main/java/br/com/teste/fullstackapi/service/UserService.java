package br.com.teste.fullstackapi.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.teste.fullstackapi.dto.UserUpdateRequest;
import br.com.teste.fullstackapi.model.Role;
import br.com.teste.fullstackapi.model.User;
import br.com.teste.fullstackapi.repository.UserRepository;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; 

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        // Criptografa a senha antes de salvar
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        
        // Define um papel padrão para novos usuários
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }
        
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserUpdateRequest userDetails) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + id));

    // Atualiza os campos permitidos
    user.setNome(userDetails.getNome());
    user.setRole(userDetails.getRole());

    return userRepository.save(user);
}

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}