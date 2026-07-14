package com.spring_boot_jpa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_boot_jpa.entity.User;
import com.spring_boot_jpa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @MockitoBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John", "Doe", "john.doe@example.com", "123 Main St", "New York");
        user.setId(1L);
    }

    @Test
    void whenCreateUser_thenReturnCreated() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void whenGetAllUsers_thenReturnUserList() throws Exception {
        User user2 = new User("Jane", "Smith", "jane.smith@example.com", "456 Oak Ave", "Los Angeles");
        user2.setId(2L);

        List<User> users = Arrays.asList(user, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void whenGetUserById_thenReturnNotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        User userDetails = new User("John Updated", "Doe Updated", "john.updated@example.com", "789 Pine Rd", "Chicago");
        user.setFirstName("John Updated");
        user.setLastName("Doe Updated");
        user.setEmail("john.updated@example.com");
        user.setAddress("789 Pine Rd");
        user.setCity("Chicago");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John Updated"))
                .andExpect(jsonPath("$.lastName").value("Doe Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void whenUpdateUserWithInvalidId_thenReturnNotFound() throws Exception {
        User userDetails = new User("John Updated", "Doe Updated", "john.updated@example.com", "789 Pine Rd", "Chicago");

        when(userService.updateUser(eq(999L), any(User.class))).thenReturn(null);

        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any(User.class));
    }

    @Test
    void whenDeleteUser_thenReturnNoContent() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void whenDeleteUserWithInvalidId_thenReturnNotFound() throws Exception {
        when(userService.deleteUser(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(999L);
    }
}
