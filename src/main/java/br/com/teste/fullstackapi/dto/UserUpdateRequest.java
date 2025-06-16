package br.com.teste.fullstackapi.dto;

import br.com.teste.fullstackapi.model.Role;
import jakarta.validation.constraints.NotEmpty;

public class UserUpdateRequest {
    @NotEmpty(message = "O nome não pode ser vazio.")
    private String nome;
    private Role role;
    // Não incluímos email ou senha aqui, pois são tratados separadamente.
    
    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}