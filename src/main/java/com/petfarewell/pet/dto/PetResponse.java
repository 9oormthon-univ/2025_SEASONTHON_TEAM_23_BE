package com.petfarewell.pet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetResponse {
    private Long id;
    private String name;
    private String breed;
    private String personality;
}
