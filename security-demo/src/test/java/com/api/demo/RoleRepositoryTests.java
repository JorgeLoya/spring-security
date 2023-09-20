package com.api.demo;

import com.api.demo.dao.RoleRepository;
import com.api.demo.pojo.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateRoles() {
        Role admin = new Role("ROLE_ADMIN");
        Role editor = new Role("ROLE_EDITOR");
        Role customer = new Role("ROLE_CUSTOMER");

        repo.saveAll(List.of(admin, editor, customer));

        long count = repo.count();
        assertEquals(3, count);
    }
}