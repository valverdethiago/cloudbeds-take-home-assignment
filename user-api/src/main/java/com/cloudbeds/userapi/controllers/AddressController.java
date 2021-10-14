package com.cloudbeds.userapi.controllers;

import com.cloudbeds.userapi.exceptions.EntityNotFoundException;
import com.cloudbeds.userapi.model.Address;
import com.cloudbeds.userapi.repository.AddressRepository;
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
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    @GetMapping
    public ResponseEntity<List<Address>> listAddresses() {
        List<Address> addresses = addressRepository.findAll();
        if(addresses == null || addresses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddress(@PathVariable Long id) {
        return addressRepository.findById(id)
            .map(address -> ResponseEntity.ok(address))
            .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> replaceAddress(@PathVariable Long id, @RequestBody Address newAddress) {
        return addressRepository.findById(id)
            .map(address -> {
                address.setAddress1(newAddress.getAddress1());
                address.setAddress2(newAddress.getAddress2());
                address.setCity(newAddress.getCity());
                address.setCountry(newAddress.getCountry());
                address.setState(newAddress.getState());
                address.setZip(newAddress.getZip());
                return ResponseEntity.ok(addressRepository.save(address));
            })
            .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address newAddress) {
        try {
            return ResponseEntity.ok(addressRepository.save(newAddress));
        }
        catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAddress(@PathVariable Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        try {
            addressRepository.delete(address);
            return ResponseEntity.ok().build();
        }
        catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

}
