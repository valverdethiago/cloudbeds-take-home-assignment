package com.cloudbeds.userapi.controllers;

import com.cloudbeds.userapi.exceptions.EntityNotFoundException;
import com.cloudbeds.userapi.model.User;
import com.cloudbeds.userapi.repository.AddressRepository;
import com.cloudbeds.userapi.repository.UserRepository;
import com.cloudbeds.userapi.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AddressRepository addressRepository;

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userRepository.findAll();
        if(users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> ResponseEntity.ok(user))
            .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> replaceUser(@PathVariable Long id, @RequestBody User newUser) {
        return userRepository.findById(id)
            .map(user -> {
                user.setEmail(newUser.getEmail());
                user.setFirstName(newUser.getFirstName());
                user.setPassword(newUser.getPassword());
                user.setLastName(newUser.getLastName());
                user.setAddresses(newUser.getAddresses());
                return ResponseEntity.ok(userRepository.save(user));
            })
            .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser)  {
        try {
            return ResponseEntity.ok(userService.createUser(newUser));
        }
        catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        try {
            userRepository.delete(user);
            return ResponseEntity.ok().build();
        }
        catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/attachAddress/{addressId}")
    public void attachUserToAddress(@PathVariable Long id, @PathVariable Long addressId) {
        addressRepository.findById(addressId).orElseThrow(() -> new EntityNotFoundException());
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        userRepository.attachAddressToUser(id, addressId);
    }

    @GetMapping("searchByCountry/{country}")
    public ResponseEntity<List<User>> findUsersByCountry(@PathVariable String country) {
        List<User> users = userRepository.findByCountry(country);
        if(users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
}
