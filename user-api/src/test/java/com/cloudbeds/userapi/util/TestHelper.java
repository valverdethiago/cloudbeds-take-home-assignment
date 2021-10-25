package com.cloudbeds.userapi.util;

import com.cloudbeds.userapi.model.Address;
import com.cloudbeds.userapi.model.User;
import com.github.javafaker.Faker;

import java.util.*;

public class TestHelper {

    private static Faker faker = new Faker();
    private static Random random = new Random();

    public static List<User> buildFakeUserList(int size) {
        List<User> result = new ArrayList<>();
        for(int i=0; i< size; i++) {
            result.add(buildFakeUser());
        }
        return result;
    }

    public static User buildFakeUser() {
        return User.builder()
            .id(random.nextLong())
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .email(faker.internet().emailAddress())
            .password(faker.internet().password())
            .addresses(new HashSet<>(buildFakeAddressList(10)))
            .build();
    }

    public static User updateFakeUser(User original) {
        original.setFirstName(faker.name().firstName());
        original.setLastName(faker.name().lastName());
        original.setEmail(faker.internet().emailAddress());
        original.setPassword(faker.internet().password());
        return original;
    }

    public static List<Address> buildFakeAddressList(int size) {
        List<Address> result = new ArrayList<>();
        for (int i=0; i<size; i++) {
            result.add(buildFakeAddress());
        }
        return result;
    }

    public static Address buildFakeAddress() {
        return Address.builder()
            .id(random.nextLong())
            .address1(faker.address().streetAddress())
            .address2(faker.address().secondaryAddress())
            .city(faker.address().city())
            .state(faker.address().state())
            .country(faker.address().country())
            .zip(faker.address().zipCode())
            .build();
    }

    public static Address updateFakeAddress(Address original) {
        original.setAddress1(faker.address().streetAddress());
        original.setAddress2(faker.address().secondaryAddress());
        original.setCity(faker.address().city());
        original.setState(faker.address().state());
        original.setCountry(faker.address().country());
        original.setZip(faker.address().zipCode());
        return original;
    }
}
