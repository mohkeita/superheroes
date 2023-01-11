package io.mohkeita.superheroes.antiHero.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class AntiHeroDto {

    private UUID id;
    @NotNull(message = "First Name can't be empty")
    private String firstname;
    private String lastname;
    private String house;
    private String knownAs;
}
