package com.cloudbeds.userapi.controllers;

import com.cloudbeds.userapi.model.Address;
import com.cloudbeds.userapi.repository.AddressRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static com.cloudbeds.userapi.util.TestHelper.buildFakeAddress;
import static com.cloudbeds.userapi.util.TestHelper.buildFakeAddressList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @Value("${app.topic.users}")
    private String topicName;
    @MockBean
    private AddressRepository addressRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /address success")
    public void shouldListAllAddresses() throws Exception {
        //Arrange
        List<Address> addresses = buildFakeAddressList(10);
        doReturn(addresses).when(addressRepository).findAll();
        // Act
        mockMvc.perform(get("/address"))
        // Assert
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(10)));
    }
    @Test
    @DisplayName("GET /address success")
    public void shouldReturnNoContentWhenNoAddressesAreFound() throws Exception {
        //Arrange
        doReturn(null).when(addressRepository).findAll();
        // Act
        mockMvc.perform(get("/address"))
            // Assert
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /user/${id} success")
    public void shouldReturnTheAddress() throws Exception {
        //Arrange
        Address address = buildFakeAddress();
        doReturn(Optional.of(address)).when(addressRepository).findById(address.getId());
        // Act
        mockMvc.perform(get("/address/{id}", address.getId()))
            // Assert
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id", is(address.getId())))
            .andExpect(jsonPath("address1", is(address.getAddress1())))
            .andExpect(jsonPath("address2", is(address.getAddress2())))
            .andExpect(jsonPath("city", is(address.getCity())))
            .andExpect(jsonPath("country", is(address.getCountry())))
            .andExpect(jsonPath("state", is(address.getState())))
            .andExpect(jsonPath("city", is(address.getCity())));
    }

    @Test
    @DisplayName("GET /address/${id} error")
    public void shouldReturnNotFoundForInvalidUserId() throws Exception {
        //Arrange
        doReturn(Optional.empty()).when(addressRepository).findById(anyLong());
        // Act
        mockMvc.perform(get("/address/{id}", new Random().nextLong()))
            // Assert
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /address success")
    public void shouldCreateAndReturnUser() throws Exception {
        //Arrange
        Address address = buildFakeAddress();
        doReturn(address).when(addressRepository).save(address);
        //Act
        mockMvc.perform(post("/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
            //Assert
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id", is(address.getId())))
            .andExpect(jsonPath("address1", is(address.getAddress1())))
            .andExpect(jsonPath("address2", is(address.getAddress2())))
            .andExpect(jsonPath("city", is(address.getCity())))
            .andExpect(jsonPath("country", is(address.getCountry())))
            .andExpect(jsonPath("state", is(address.getState())))
            .andExpect(jsonPath("city", is(address.getCity())));
    }
    @Test
    @DisplayName("POST /address error")
    public void shouldReturnInternalErrorWhenDatabaseErrorOccurs() throws Exception {
        //Arrange
        Address address = buildFakeAddress();
        doThrow(new QueryTimeoutException("Timeout")).when(addressRepository).save(address);
        //Act
        mockMvc.perform(post("/address")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(address)))
            //Assert
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("DELETE /address/{id} success")
    public void shouldDeleteAddress() throws Exception {
        //Arrange
        Address address = buildFakeAddress();
        doReturn(Optional.of(address)).when(addressRepository).findById(address.getId());
        //Act
        mockMvc.perform(delete("/address/{id}", address.getId()))
            //Assert
            .andExpect(status().isOk());
        verify(addressRepository).delete(address);
    }

    @Test
    @DisplayName("DELETE /address/{id} error")
    public void shouldReturnNotFoundWhenAddressCantBeFound() throws Exception {
        //Arrange
        doReturn(Optional.empty()).when(addressRepository).findById(anyLong());
        //Act
        mockMvc.perform(delete("/address/{id}", new Random().nextLong()))
            //Assert
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /address error")
    public void shouldReturnInternalErrorWhenDatabaseErrorOccursOnDelete() throws Exception {
        //Arrange
        Address address = buildFakeAddress();
        doReturn(Optional.of(address)).when(addressRepository).findById(anyLong());
        doThrow(new QueryTimeoutException("Timeout")).when(addressRepository).delete(address);
        //Act
        mockMvc.perform(delete("/address/{id}", address.getId()))
            //Assert
            .andExpect(status().is5xxServerError());
    }


}
