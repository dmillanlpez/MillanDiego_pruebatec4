package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.model.User;

public interface IUserService {

    // Crea un usuario en la base de datos
    User createUser(User user);

    // Busca a un usuario por su correo electronico
    User findByEmail(String email);
}
