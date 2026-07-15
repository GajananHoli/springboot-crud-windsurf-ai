package com.spring_boot_jpa.repository;

import com.spring_boot_jpa.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private static int emailCounter = 0;

    @BeforeEach
    void setUp() {
        emailCounter++;
        user = new User("John", "Doe", "john.doe" + emailCounter + "@example.com", "123 Main St", "New York");
    }

    @Test
    void whenSaveUser_thenReturnUser() {
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertTrue(savedUser.getEmail().startsWith("john.doe"));
    }

    @Test
    void whenFindById_thenReturnUser() {
        User savedUser = entityManager.persistAndFlush(user);
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("John", foundUser.get().getFirstName());
    }

    @Test
    void whenFindById_thenReturnEmpty() {
        Optional<User> foundUser = userRepository.findById(999L);
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    void whenFindAll_thenReturnAllUsers() {
        User user1 = new User("John", "Doe", "john.unique1@example.com", "123 Main St", "New York");
        User user2 = new User("Jane", "Smith", "jane.unique1@example.com", "456 Oak Ave", "Los Angeles");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        
        Iterable<User> users = userRepository.findAll();
        
        assertNotNull(users);
        assertTrue(users.iterator().hasNext());
    }

    @Test
    void whenExistsById_thenReturnTrue() {
        User savedUser = entityManager.persistAndFlush(user);
        
        boolean exists = userRepository.existsById(savedUser.getId());
        
        assertTrue(exists);
    }

    @Test
    void whenExistsById_thenReturnFalse() {
        boolean exists = userRepository.existsById(999L);
        
        assertFalse(exists);
    }

    @Test
    void whenDeleteById_thenUserDeleted() {
        User savedUser = entityManager.persistAndFlush(user);
        
        userRepository.deleteById(savedUser.getId());
        
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void whenSaveUserWithDuplicateEmail_thenThrowException() {
        entityManager.persistAndFlush(user);
        
        User duplicateUser = new User("Jane", "Smith", user.getEmail(), "789 Pine Rd", "Chicago");
        
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        });
    }
}
