package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.model.Room;
import com.hackaboss.AgenciaTurismo.repository.HotelRepository;
import com.hackaboss.AgenciaTurismo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomService implements IRoomService {

    // Inyeccion de dependencia del repositorio de hoteles y el repositorio de habitaciones
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;

    // Crea una habitacion en la base de datos
    @Override
    public Room createRoom(Room room) {

        return roomRepository.save(room);
    }
    // Busca todas las habitaciones en la base de datos
    @Override
    public List<Room> getAllRooms() {

        return roomRepository.findAll();
    }

    // Busca una habitacion por su tipo
    @Override
    public Room getRoomByType(String roomType) {

        return roomRepository.findByRoomType(roomType);
    }
}
