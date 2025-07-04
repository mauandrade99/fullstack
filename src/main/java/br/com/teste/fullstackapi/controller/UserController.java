package br.com.teste.fullstackapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.teste.fullstackapi.dto.UserMapper;
import br.com.teste.fullstackapi.dto.UserResponseDTO;
import br.com.teste.fullstackapi.dto.UserUpdateRequest;
import br.com.teste.fullstackapi.model.User;
import br.com.teste.fullstackapi.service.UserService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/users") 
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @param user O objeto User vindo do corpo da requisição (@RequestBody).
     *             A anotação @Valid ativa as validações definidas no modelo (se houver).
     * @return uma ResponseEntity contendo o usuário criado e o status HTTP 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") 
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                     @Valid @RequestBody UserUpdateRequest userDetails,
                                                     Authentication authentication
    ) {
        User loggedInUser = (User) authentication.getPrincipal();
        Long performingUserId = loggedInUser.getId();

        User updatedUser = userService.updateUser(id, userDetails,performingUserId); 
     
        return ResponseEntity.ok(UserMapper.toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apenas admins podem excluir usuários
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    // Um usuário só pode ver a si mesmo, a menos que seja um ADMIN.
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id") 
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toDTO(user))) // Se encontrar, converte para DTO e retorna 200 OK
                .orElse(ResponseEntity.notFound().build()); // Se não encontrar, retorna 404 Not Found
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Listar todos os usuários é uma ação de Admin
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
               
        Page<User> userPage = userService.findAllUsers(pageable);
        
        // Converte a página de User para uma página de UserResponseDTO
        Page<UserResponseDTO> userDtoPage = userPage.map(UserMapper::toDTO);
        
        return ResponseEntity.ok(userDtoPage);
    }

}