package com.cloudbeds.userfakefeeder.service.impl;

import com.cloudbeds.userfakefeeder.service.UserApiClientService;
import com.cloudbeds.userfakefeeder.service.UserFeederService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class UserFeederServiceImpl implements UserFeederService {

    @Autowired
    private UserApiClientService userApiClientService;

    @Override
    @Scheduled(fixedRate = 5000)
    public void createAndSendFakeUser() {
        log.info("Running");
        userApiClientService.createAndSendFakeUser();
    }
}
