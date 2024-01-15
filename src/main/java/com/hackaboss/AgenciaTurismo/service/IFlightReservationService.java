package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.FlightReservationDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdFlightReservationDTO;
import com.hackaboss.AgenciaTurismo.model.FlightReservation;

import java.util.List;

public interface IFlightReservationService {

    // Permite crear una reserva de un avion
    FlightReservation createFlightReservation(FlightReservationDTO flightReservationDTO);

    // Permite obtener las reservas mediante su id desde la bbdd
    FlightReservation getFlightReservationId(Long id);

    // Permite obtener todas las reservas de un avion desde la bbdd
    List<FlightReservation> getAllFlightReservations();

    // Permite actualizar una reserva de un avion
    FlightReservation updateFlightReservation(Long id, UpdFlightReservationDTO updFlightReservationDTO);

    // Permite eliminar una reserva
    FlightReservation deleteFlightReservation(Long id);

}
