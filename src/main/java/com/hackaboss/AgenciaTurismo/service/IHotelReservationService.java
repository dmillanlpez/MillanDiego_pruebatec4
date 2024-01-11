package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.HotelReservationDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdHotelReservationDTO;
import com.hackaboss.AgenciaTurismo.model.HotelReservation;

import java.util.List;

public interface IHotelReservationService {
    // Obtener todas las reservas
    List<HotelReservation> getAllReservations();

    // Crear una reserva
    HotelReservation createReservation(HotelReservationDTO hotelReservationDTO);

    // Actualizar una reserva
    HotelReservation updateReservationID(Long id, UpdHotelReservationDTO updHotelReservationDTO);

    // Eliminar una reserva
    HotelReservation deleteReservation(Long id);

    // Obtener una reserva por su id
    HotelReservation getReservationById(Long id);
}
