package io.mohkeita.superheroes.antiHero.controller;

import io.mohkeita.superheroes.antiHero.dto.AntiHeroDto;
import io.mohkeita.superheroes.antiHero.entity.AntiHeroEntity;
import io.mohkeita.superheroes.antiHero.service.AntiHeroService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@CrossOrigin(allowedHeaders = "Content-type")
@RestController
@RequestMapping("api/v1/anti-heroes")
public class AntiHeroController {
    private final AntiHeroService service;

    private final ModelMapper mapper;

    @GetMapping("/{id}")
    public AntiHeroDto getAntiHeroById(@PathVariable("id") UUID id) {
        return convertToDto(service.findAntiHeroById(id));
    }

    @PostMapping
    public AntiHeroDto postAntiHero(@Valid @RequestBody AntiHeroDto antiHeroDto) {
        var entity = convertToEntity(antiHeroDto);
        var antiHero = service.addAntiHero(entity);

        return convertToDto(antiHero);
    }

    @PutMapping("/{id}")
    public void putAntiHero(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AntiHeroDto antiHeroDto) {
        if (!id.equals(antiHeroDto.getId())) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "ID doest not match");

        var AntiHeroEntity = convertToEntity(antiHeroDto);
        service.updateAntiHero(id, AntiHeroEntity);
    }

    @DeleteMapping("/{id}")
    public void deleteAntiHeroById(@PathVariable("id") UUID id) {
        service.removeAntiHeroById(id);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<AntiHeroDto> getAntiHeroes(Pageable pageable) {
        int toSkip = pageable.getPageSize() * pageable.getPageNumber();


        var antiHeroList = StreamSupport
                .stream(service.findAllAntiHeroes().spliterator(), false)
                .skip(toSkip).limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return antiHeroList.stream()
               .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AntiHeroDto convertToDto(AntiHeroEntity antiHero) {
        return mapper.map(antiHero, AntiHeroDto.class);
    }

    private AntiHeroEntity convertToEntity(AntiHeroDto antiHero) {
        return mapper.map(antiHero, AntiHeroEntity.class);
    }
}
