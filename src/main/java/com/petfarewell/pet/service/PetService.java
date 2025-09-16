package com.petfarewell.pet.service;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import com.petfarewell.pet.dto.PetRequest;
import com.petfarewell.pet.dto.PetResponse;
import com.petfarewell.pet.entity.Pet;
import com.petfarewell.pet.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // 활성화 펫 설정 메서드
    @Transactional
    public void setActivePet(Long userId, Long petId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        List<Pet> pets = petRepository.findByUser(user);

        for (Pet pet : pets) {
            pet.setActive(pet.getId().equals(petId));
        }
    }

    @Transactional
    public void createPet(Long userId, PetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean isFirstPet = petRepository.findByUser(user).isEmpty();

        Pet pet = Pet.builder()
                .user(user)
                .name(request.getName())
                .breed(request.getBreed())
                .personality(request.getPersonality())
                .isActive(isFirstPet)
                .build();

        petRepository.save(pet);
    }

    public List<PetResponse> getUserPets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return petRepository.findByUser(user).stream()
                .map(pet -> PetResponse.builder()
                        .id(pet.getId())
                        .name(pet.getName())
                        .breed(pet.getBreed())
                        .personality(pet.getPersonality())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("해당 반려동물을 찾을 수 없거나 삭제 권한이 없습니다."));

        petRepository.delete(pet);
    }

    @Transactional
    public Pet updatePet(Long petId, PetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("해당 반려동물을 찾을 수 없거나 수정 권한이 없습니다."));

        pet.update(request.getName(), request.getBreed(), request.getPersonality());

        return pet;
    }
}
