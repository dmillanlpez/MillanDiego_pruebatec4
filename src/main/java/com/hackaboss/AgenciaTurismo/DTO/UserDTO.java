package com.hackaboss.AgenciaTurismo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    // Atributos
    private String name;
    private String lastName;
    private String email;
    private int age;
    private String passport;

}
