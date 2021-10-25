package com.cloudbeds.userfakefeeder.service;

import com.cloudbeds.userfakefeeder.model.User;

import java.util.*;

public interface UserApiClientService {

    Optional<User> createAndSendFakeUser();
}
