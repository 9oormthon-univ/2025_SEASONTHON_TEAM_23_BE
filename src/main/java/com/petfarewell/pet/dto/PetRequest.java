package com.petfarewell.pet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetRequest {
    private String name;
    private String breed;
    private String personality;
}
