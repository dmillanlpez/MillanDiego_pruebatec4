package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.model.Room;

import java.util.List;

public interface IRoomService {

    // Crea una habitacion en la base de datos
    Room createRoom(Room room);

    // Busca todas las habitaciones en la base de datos
    List<Room> getAllRooms();

    // Busca una habitacion por su tipo
    Room getRoomByType(String roomType);
}
