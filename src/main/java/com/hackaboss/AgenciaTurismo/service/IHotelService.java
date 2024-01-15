package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.HotelDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdHotelDTO;
import com.hackaboss.AgenciaTurismo.model.Hotel;
import java.time.LocalDate;
import java.util.List;

public interface IHotelService {

    // Crea una habitacion en la base de datos
    Hotel createHotel(HotelDTO hotel);

    // Busca en la base de datos las habitaciones mediante un filtro de fecha y localizacion
    List<Hotel> getHotelsByLocationAndDate(LocalDate avaliableDateFrom, LocalDate avaliableDateTo, String location, boolean booked);

    // Busca todas las habitaciones en la base de datos
    List<Hotel> getAllHotels();

    // Borra una habitacion en la base de datos
    void deleteHotelById(Long id);

    // Edita una habitacion en la base de datos
    Hotel editHotelById(Long id, UpdHotelDTO hotel);

    // Busca una habitacion por su id
    Hotel getHotelById(Long id);

}
