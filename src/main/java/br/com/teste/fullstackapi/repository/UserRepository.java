package br.com.teste.fullstackapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.teste.fullstackapi.model.User;

/**
 * Repository para a entidade User.
 * JpaRepository<User, Long> significa que ele gerencia a entidade User,
 * cuja chave primária (ID) é do tipo Long.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * @param email O e-mail a ser pesquisado.
     * @return um Optional<User> contendo o usuário, se existir.
     */
    Optional<User> findByEmail(String email);

}