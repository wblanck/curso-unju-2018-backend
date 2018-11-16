package com.softlogia.curso.unju2018.backend.resources;

import com.softlogia.curso.unju2018.backend.model.Role;
import com.softlogia.curso.unju2018.backend.model.User;
import com.softlogia.curso.unju2018.backend.repositories.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author wblanck
 */
@RestController
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserRepository userRepository;

    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * POST /users : Creates a new user.
     *
     * @param user
     * @return
     * @throws java.net.URISyntaxException
     */
    @PostMapping("/users")
    public ResponseEntity createUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.debug("REST request to save User : {}", user);

        if (user.getId() != null) {
            return ResponseEntity.badRequest().body("A new user cannot already have an ID");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByUsername(user.getUsername().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already used.");
        } else if (user.getPassword() == null || user.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("Password min length is 8 chars");
        } else {
            User newUser = userRepository.saveAndFlush(user);
            return ResponseEntity.created(new URI("/users/" + newUser.getId()))
                    .body(newUser);
        }
    }

    /**
     * PUT /users : Updates an existing User.
     *
     * @param user
     * @return
     */
    @PutMapping("/users")
    public ResponseEntity updateUser(@Valid @RequestBody User user) {
        log.debug("REST request to update User : {}", user);
        Optional<User> existingUser = userRepository.findOneByUsername(user.getUsername().toLowerCase());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body("Username already used.");
        } else if (user.getPassword() == null || user.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("Password min length is 8 chars");
        }
        User updatedUser = userRepository.saveAndFlush(user);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * GET /users : get all users.
     *
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("REST request to get all users.");
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * @return a string list of all the roles
     */
    @GetMapping("/users/roles")
    public List<Role> getAuthorities() {
        log.debug("REST request to get all roles.");
        List<Role> roles = Arrays.asList(Role.values());
        return roles;
    }

    /**
     * GET /users/:login : get the "login" user.
     *
     * @param username
     * @return the ResponseEntity with status 200 (OK) and with body the "login"
     * user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{username:^[_'.@A-Za-z0-9-]*$}")
    public ResponseEntity getUser(@PathVariable String username) {
        log.debug("REST request to get User : {}", username);
        Optional<User> user = userRepository.findOneByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /users/:id
     *
     * @param id
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete User: {}", id);
        userRepository.deleteById(id);
        return ResponseEntity.ok(null);
    }
}
