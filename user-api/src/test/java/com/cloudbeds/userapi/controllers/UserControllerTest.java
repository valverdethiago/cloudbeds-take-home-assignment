package com.cloudbeds.userapi.controllers;

import com.cloudbeds.userapi.model.Address;
import com.cloudbeds.userapi.model.User;
import com.cloudbeds.userapi.repository.AddressRepository;
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
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static com.cloudbeds.userapi.util.TestHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private AddressRepository addressRepository;
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
    @DisplayName("GET /user success")
    public void shouldReturnNoContentWhenNoUsersAreFound() throws Exception {
        //Arrange
        List<User> users = buildFakeUserList(10);
        doReturn(null).when(userRepository).findAll();
        // Act
        mockMvc.perform(get("/user"))
            // Assert
            .andExpect(status().isNoContent());
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
    @DisplayName("PUT /user success")
    public void shouldUpdateAndReturnUser() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        updateFakeUser(user);
        doReturn(user).when(userRepository).save(user);
        // Act
        mockMvc.perform(put("/user/{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            // Assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /user invalid id")
    public void shouldReturnNoContentWhenUpdatingAUser() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(Optional.empty()).when(userRepository).findById(user.getId());
        doReturn(user).when(userRepository).save(user);
        // Act
        mockMvc.perform(put("/user/{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            // Assert
            .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("PUT /user error")
    public void shouldReturnInternalErrorWhenDatabaseErrorOccursOnUpdate() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doThrow(new QueryTimeoutException("Timeout")).when(userRepository).save(user);
        //Act
        mockMvc.perform(put("/user/{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            //Assert
            .andExpect(status().is5xxServerError());
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

    @Test
    @DisplayName("POST /user error")
    public void shouldReturnInternalErrorWhenDatabaseErrorOccurs() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doThrow(new QueryTimeoutException("Timeout")).when(userRepository).save(user);
        //Act
        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            //Assert
            .andExpect(status().is5xxServerError());
        verify(kafkaTemplate, times(0)).send(topicName, objectMapper.writeValueAsString(user));
    }

    @Test
    @DisplayName("DELETE /user/{id} success")
    public void shouldDeleteUser() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        //Act
        mockMvc.perform(delete("/user/{id}", user.getId()))
            //Assert
            .andExpect(status().isOk());
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("DELETE /user/{id} error")
    public void shouldReturnNotFoundWhenUserCantBeFound() throws Exception {
        //Arrange
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());
        //Act
        mockMvc.perform(delete("/user/{id}", new Random().nextLong()))
            //Assert
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /user error")
    public void shouldReturnInternalErrorWhenDatabaseErrorOccursOnDelete() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doThrow(new QueryTimeoutException("Timeout")).when(userRepository).delete(user);
        //Act
        mockMvc.perform(delete("/user/{id}", user.getId()))
            //Assert
            .andExpect(status().is5xxServerError());
        verify(kafkaTemplate, times(0)).send(topicName, objectMapper.writeValueAsString(user));
    }

    @Test
    @DisplayName("PUT /user/{id}/attachAddress/{addressId} success")
    public void shouldAttachAddressToUser() throws Exception {
        //Arrange
        User user = buildFakeUser();
        Address address = buildFakeAddress();
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doReturn(Optional.of(address)).when(addressRepository).findById(anyLong());
        //Act
        mockMvc.perform(put("/user/{id}/attachAddress/{addressId}", user.getId(), address.getId()))
            //Assert
            .andExpect(status().isOk());
        verify(userRepository).attachAddressToUser(user.getId(), address.getId());
    }

    @Test
    @DisplayName("PUT /user/{id}/attachAddress/{addressId} invalid address")
    public void shouldReturnNotFoundWhenAttachAddressToUserWithInvalidAddress() throws Exception {
        //Arrange
        User user = buildFakeUser();
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doReturn(Optional.empty()).when(addressRepository).findById(anyLong());
        //Act
        mockMvc.perform(put("/user/{id}/attachAddress/{addressId}", user.getId(), new Random().nextLong()))
            //Assert
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /user/{id}/attachAddress/{addressId} invalid user")
    public void shouldReturnNotFoundWhenAttachAddressToUserWithInvalidUser() throws Exception {
        //Arrange
        Address address = buildFakeAddress();
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());
        doReturn(Optional.of(address)).when(addressRepository).findById(anyLong());
        //Act
        mockMvc.perform(put("/user/{id}/attachAddress/{addressId}", new Random().nextLong(), address.getId()))
            //Assert
            .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("GET /searchByCountry/{country} success")
    public void shouldReturnUsersByCountry() throws Exception {
        //Arrange
        List<User> users = buildFakeUserList(10);
        doReturn(users).when(userRepository).findByCountry(anyString());
        //Act
        mockMvc.perform(get("/user/searchByCountry/{country}", "US"))
            //Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    @DisplayName("GET searchByCountry/{country} empty result")
    public void shouldReturnNoContentWhenNoUsersAreFoundOnSearch() throws Exception {
        //Arrange
        doReturn(null).when(userRepository).findByCountry(anyString());
        //Act
        mockMvc.perform(get("/user/searchByCountry/{country}", "US"))
            //Assert
            .andExpect(status().isNoContent());
    }





}
