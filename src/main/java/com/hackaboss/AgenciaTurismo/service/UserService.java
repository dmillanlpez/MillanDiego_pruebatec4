package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.model.User;
import com.hackaboss.AgenciaTurismo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    // Inyeccion de dependencias
    @Autowired
    private UserRepository userRepo;

    // Crea un usuario en la base de datos
    @Override
    public User createUser(User user) {

        return userRepo.save(user);
    }

    // Busca a un usuario por su correo electronico
    @Override
    public User findByEmail(String email) {

        return userRepo.findByEmail(email);
    }
}
