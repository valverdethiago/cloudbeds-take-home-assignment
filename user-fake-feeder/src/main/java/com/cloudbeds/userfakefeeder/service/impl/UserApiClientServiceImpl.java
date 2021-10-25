package com.cloudbeds.userfakefeeder.service.impl;

import com.cloudbeds.userfakefeeder.model.User;
import com.cloudbeds.userfakefeeder.service.UserApiClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.cloudbeds.userfakefeeder.util.FakeObjectFactory.*;

@Service
@Slf4j
public class UserApiClientServiceImpl implements UserApiClientService {

    @Value("${app.server.url}")
    private String serverUrl;

    @Override
    public Optional<User> createAndSendFakeUser() {
        log.info(serverUrl);
        RestTemplate restTemplate = new RestTemplate();
        User user = buildFakeUser();
        ResponseEntity<User> response = restTemplate.postForEntity(serverUrl, user, User.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            User created = response.getBody();
            log.info("User created successfully: {}", created);
            return Optional.of(created);
        }
        log.info("Error creating user ", response.getStatusCode());
        return Optional.empty();
    }
}
