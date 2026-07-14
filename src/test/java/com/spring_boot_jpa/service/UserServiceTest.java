package com.spring_boot_jpa.service;

import com.spring_boot_jpa.entity.User;
import com.spring_boot_jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John", "Doe", "john.doe@example.com", "123 Main St", "New York");
        user.setId(1L);
    }

    @Test
    void whenCreateUser_thenReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
        assertEquals("Doe", createdUser.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenGetAllUsers_thenReturnUserList() {
        User user2 = new User("Jane", "Smith", "jane.smith@example.com", "456 Oak Ave", "Los Angeles");
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John", users.get(0).getFirstName());
        assertEquals("Jane", users.get(1).getFirstName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void whenGetUserById_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
        assertEquals("Doe", foundUser.get().getLastName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetUserById_thenReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(999L);

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() {
        User userDetails = new User("John Updated", "Doe Updated", "john.updated@example.com", "789 Pine Rd", "Chicago");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, userDetails);

        assertNotNull(updatedUser);
        assertEquals("John Updated", updatedUser.getFirstName());
        assertEquals("Doe Updated", updatedUser.getLastName());
        assertEquals("john.updated@example.com", updatedUser.getEmail());
        assertEquals("789 Pine Rd", updatedUser.getAddress());
        assertEquals("Chicago", updatedUser.getCity());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenUpdateUserWithInvalidId_thenReturnNull() {
        User userDetails = new User("John Updated", "Doe Updated", "john.updated@example.com", "789 Pine Rd", "Chicago");
        
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User updatedUser = userService.updateUser(999L, userDetails);

        assertNull(updatedUser);
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenDeleteUser_thenReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteUserWithInvalidId_thenReturnFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}
