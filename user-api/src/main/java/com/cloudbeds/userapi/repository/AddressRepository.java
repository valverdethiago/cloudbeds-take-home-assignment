package com.cloudbeds.userapi.repository;

import com.cloudbeds.userapi.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "address", path = "address")
public interface AddressRepository extends CrudRepository<Address, Long> {
}
