package com.softlogia.curso.unju2018.backend.repositories;

import com.softlogia.curso.unju2018.backend.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author wblanck
 */
public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findOneByUsername(String toLowerCase);

}
