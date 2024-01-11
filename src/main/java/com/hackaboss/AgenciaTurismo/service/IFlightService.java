package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.FlightDTO;
import com.hackaboss.AgenciaTurismo.model.Flight;

import java.time.LocalDate;
import java.util.List;

public interface IFlightService {

    // Crea un avion en la base de datos
    Flight createFlight(FlightDTO flightDto);

    // Permite buscar todos los aviones en la base de datos
    List<Flight> getAllFlights();

    // Permite editar un avion en la base de datos
    Flight updateFlight(Long id, FlightDTO flightDTO);

    // Permite buscar por ID un avion en la base de datos
    Flight getFlightById(Long id);

    // Permite borrar un avion en la base de datos
    Flight deleteFlightById(Long id);

    // Permite buscar aviones mediante un filtro de fecha y localizacion
    List<Flight> getAllFlightsByLocationAndDate(LocalDate avaliableDateFrom, LocalDate avaliableDateTo, String arrival, String departure);
}
