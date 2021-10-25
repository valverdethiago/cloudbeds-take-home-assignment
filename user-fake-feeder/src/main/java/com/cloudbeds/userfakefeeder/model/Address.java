package com.cloudbeds.userfakefeeder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private Long id;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;
}
