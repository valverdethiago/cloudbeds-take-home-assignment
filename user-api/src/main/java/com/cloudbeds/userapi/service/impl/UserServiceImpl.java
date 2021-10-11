package com.cloudbeds.userapi.service.impl;

import com.cloudbeds.userapi.model.User;
import com.cloudbeds.userapi.repository.UserRepository;
import com.cloudbeds.userapi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${app.topic.users}")
    private String topicName;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public User createUser(User user) throws JsonProcessingException {
        User dbUser = this.userRepository.save(user);
        String json = objectMapper.writeValueAsString(dbUser);
        kafkaTemplate.send(topicName, json);
        return dbUser;

    }
}
