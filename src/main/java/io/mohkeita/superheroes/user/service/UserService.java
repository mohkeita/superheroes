package io.mohkeita.superheroes.user.service;

import io.mohkeita.superheroes.exception.NotFoundException;
import io.mohkeita.superheroes.user.data.UserDto;
import io.mohkeita.superheroes.user.entity.UserEntity;
import io.mohkeita.superheroes.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository repo;
    private final ModelMapper mapper;

    public List<UserDto> findAllUsers() {
        var userEntityList = new ArrayList<>(repo.findAll());

        return userEntityList
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(final UUID id) {
        var user = repo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("User by id " + id + " was not found")
                );

        return convertToDto(user);
    }

    public UserDto createUser(UserDto userDto, String password) throws NoSuchAlgorithmException {
        var user = convertToEntity(userDto);

        if(password.isBlank()) throw  new IllegalArgumentException("Password is required.");

        var existsEmail = repo.selectExistsEmail(user.getEmail());
        if(existsEmail) throw new BadRequestException("User with email " + userDto.getEmail() + " already exists.");

        byte[] salt = createSalt();
        byte[] hashPassword = createPasswordHash(password, salt);

        user.setStoredSalt(salt);
        user.setStoredHash(hashPassword);

        repo.save(user);

        return convertToDto(user);
    }

    public void updateUser(UUID id, UserDto userDto, String password) throws NoSuchAlgorithmException {
        var user = findOrThrow(id);
        var userParam = convertToEntity(userDto);

        user.setEmail(userParam.getEmail());
        user.setMobileNumber(userParam.getMobileNumber());

        if(!password.isBlank()) {
            byte[] salt = createSalt();
            byte[] hashPassword = createPasswordHash(password, salt);

            user.setStoredSalt(salt);
            user.setStoredHash(hashPassword);
        }

        repo.save(user);
    }

    public void removeUserById(UUID id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    private UserEntity findOrThrow(final UUID id) {
        return repo.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("User by id " + id + " was not found")
                );

    }

    private byte[] createPasswordHash(String password, byte[] salt) throws NoSuchAlgorithmException {
        var md = MessageDigest.getInstance("SHA-256");
        md.update(salt);

        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] createSalt() {
        var random = new SecureRandom();
        var salt = new byte[128];
        random.nextBytes(salt);

        return salt;
    }
    private UserDto convertToDto(UserEntity entity) {
        return mapper.map(entity, UserDto.class);
    }

    private UserEntity convertToEntity(UserDto dto) {
        return mapper.map(dto, UserEntity.class);
    }
}