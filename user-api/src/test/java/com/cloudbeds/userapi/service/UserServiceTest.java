package com.cloudbeds.userapi.service;


import com.cloudbeds.userapi.model.User;
import com.cloudbeds.userapi.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static com.cloudbeds.userapi.util.TestHelper.buildFakeUser;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    public void shouldCreateUser() throws JsonProcessingException {
        // Arrange
        User user = buildFakeUser();
        doReturn(user).when(userRepository).save(user);
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        doReturn(future).when(kafkaTemplate).send(anyString(), anyString());
        //Act
        User dbUser = userService.createUser(user);
        Assertions.assertNotNull(dbUser);
        verify(userRepository).save(user);
    }
}
