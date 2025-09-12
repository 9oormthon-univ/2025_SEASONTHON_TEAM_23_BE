package com.petfarewell.pet.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUser(User user);
    Optional<Pet> findByUserAndIsActiveTrue(User user);
}
