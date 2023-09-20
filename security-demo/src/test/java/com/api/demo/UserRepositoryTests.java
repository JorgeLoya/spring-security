package com.api.demo;

import com.api.demo.dao.UserRepository;
import com.api.demo.pojo.Role;
import com.api.demo.pojo.User;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repo;

    @Test
    public void testCreateEditor() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("pass");

        User user = new User("editor@email.com", password);
        user.setName("Editor");
        user.addRole(new Role("ROLE_EDITOR", 2));

        User savedUser = repo.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateCustomer() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("pass");

        User user = new User("customer@email.com", password);
        user.setName("Customer");
        user.addRole(new Role("ROLE_CUSTOMER", 3));

        User savedUser = repo.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateAdmin() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("pass");

        User user = new User("admin@email.com", password);
        user.setName("Admin");
        user.addRole(new Role("ROLE_ADMIN", 1));
        user.addRole(new Role("ROLE_EDITOR", 2));
        user.addRole(new Role("ROLE_CUSTOMER", 3));

        User savedUser = repo.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

}