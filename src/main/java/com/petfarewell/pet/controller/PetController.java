package com.petfarewell.pet.controller;

import com.petfarewell.auth.security.CustomUserDetails;
import com.petfarewell.pet.dto.PetRequest;
import com.petfarewell.pet.dto.PetResponse;
import com.petfarewell.pet.entity.Pet;
import com.petfarewell.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "펫 페르소나 API", description = "펫 등록 및 전환을 위한 목록 조회 가능한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    @Operation(summary = "반려동물 등록", description = "반려동물의 이름, 품종, 성격을 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> createPet(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody PetRequest request) {
        petService.createPet(userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "반려동물 전체 조회", description = "사용자의 반려동물 전체 리스트를 반환합니다.")
    @GetMapping
    public ResponseEntity<List<PetResponse>> getPets(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PetResponse> pets = petService.getUserPets(userDetails.getId());
        return ResponseEntity.ok(pets);
    }

    @PatchMapping("/{petId}/activate")
    public ResponseEntity<String> activatePet(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable("petId") Long petId){
        petService.setActivePet(userDetails.getId(), petId);
        return ResponseEntity.ok("활성화된 반려동물이 변경되었습니다.");
    }

    @Operation(summary = "반려동물 삭제", description = "등록된 반려동물을 삭제합니다.")
    @DeleteMapping("/delete/{petId}")
    public ResponseEntity<String> deletePet(@PathVariable("petId") Long petId) {
        petService.deletePet(petId);
        return ResponseEntity.ok("반려동물이 삭제되었습니다.");
    }

    @Operation(summary = "반려동물 수정", description = "등록된 반려동물의 정보를 수정합니다.")
    @PutMapping("/update/{petId}")
    public ResponseEntity<PetResponse> putPet(@PathVariable("petId") Long petId,
                                              @RequestBody PetRequest request) {
        Pet updatedPet = petService.updatePet(petId, request);

        PetResponse response = PetResponse.builder()
                .id(updatedPet.getId())
                .name(updatedPet.getName())
                .breed(updatedPet.getBreed())
                .personality(updatedPet.getPersonality())
                .build();

        return ResponseEntity.ok(response);
    }
}
