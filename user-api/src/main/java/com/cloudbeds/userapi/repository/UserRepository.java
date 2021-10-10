package com.cloudbeds.userapi.repository;

import com.cloudbeds.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(value = "insert into rl_users_addresses (user_id, address_id) VALUES (:user_id, :address_id)", nativeQuery = true)
    @Transactional
    void attachAddressToUser(@Param("user_id") Long userId, @Param("address_id") Long addressId);

    @Query(" select user from User user " +
           "   join user.addresses address " +
           "  where address.country = :country")
    List<User> findByCountry(@Param("country") String country);
}
