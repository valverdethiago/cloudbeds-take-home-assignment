package com.cloudbeds.userapi.service;

import com.cloudbeds.userapi.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService {
    User createUser(User user) throws JsonProcessingException;
}
