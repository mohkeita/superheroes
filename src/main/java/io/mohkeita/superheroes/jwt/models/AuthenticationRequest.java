package io.mohkeita.superheroes.jwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class AuthenticationRequest implements Serializable {
    private String email;
    private String password;
}
