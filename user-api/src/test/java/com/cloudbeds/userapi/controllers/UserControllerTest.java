package com.cloudbeds.userapi.controllers;

import com.cloudbeds.userapi.model.User;
import com.cloudbeds.userapi.repository.UserRepository;
import com.cloudbeds.userapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static com.cloudbeds.userapi.util.TestHelper.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Value("${app.topic.users}")
    private String topicName;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /user success")
    public void shouldListAllUsers() throws Exception {
        //Arrange
        List<User> users = buildFakeUserList(10);
        doReturn(users).when(userRepository).findAll();
        // Act
        mockMvc.perform(get("/user"))
        // Assert
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    @DisplayName("GET /user/${id} success")
    public void shouldReturnTheUser() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        // Act
        mockMvc.perform(get("/user/{id}", user.getId()))
            // Assert
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id", is(user.getId())))
            .andExpect(jsonPath("email", is(user.getEmail())))
            .andExpect(jsonPath("firstName", is(user.getFirstName())))
            .andExpect(jsonPath("lastName", is(user.getLastName())))
            .andExpect(jsonPath("password", is(user.getPassword())))
            .andExpect(jsonPath("addresses", hasSize(user.getAddresses().size())));
    }

    @Test
    @DisplayName("GET /user/${id} error")
    public void shouldReturnNotFoundForInvalidUserId() throws Exception {
        //Arrange
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());
        // Act
        mockMvc.perform(get("/user/{id}", new Random().nextLong()))
            // Assert
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /user success")
    public void shouldCreateAndReturnUser() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(user).when(userRepository).save(user);
        //Act
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            //Assert
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id", is(user.getId())))
            .andExpect(jsonPath("email", is(user.getEmail())))
            .andExpect(jsonPath("firstName", is(user.getFirstName())))
            .andExpect(jsonPath("lastName", is(user.getLastName())))
            .andExpect(jsonPath("password", is(user.getPassword())))
            .andExpect(jsonPath("addresses", hasSize(user.getAddresses().size())));
        verify(kafkaTemplate).send(topicName, objectMapper.writeValueAsString(user));

    }





}
