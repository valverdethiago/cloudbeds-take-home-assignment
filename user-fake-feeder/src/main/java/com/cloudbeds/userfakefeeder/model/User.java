package com.cloudbeds.userfakefeeder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<Address> addresses;

}
