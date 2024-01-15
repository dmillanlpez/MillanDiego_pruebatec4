package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.FlightDTO;
import com.hackaboss.AgenciaTurismo.model.Flight;
import com.hackaboss.AgenciaTurismo.model.FlightReservation;
import com.hackaboss.AgenciaTurismo.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class FlightService implements IFlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightReservationService flightReservationService;


    @Override
    public Flight createFlight(FlightDTO flightDTO) {
        try {
            if (flightDTO.getDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Cannot create a flight with a date in the past.");
            }

            // Creacion de un vuelo
            Flight flight = new Flight();

            flight.setArrival(flightDTO.getArrival());
            flight.setDeparture(flightDTO.getDeparture());

            // Construir un nombre unico usando la ciudad de llegada y la fecha
            String flightName = buildFlightName(flightDTO.getArrival(), flightDTO.getDate());
            flight.setCodFlight(generateFlightCode(flightName));

            flight.setDate(flightDTO.getDate());
            flight.setDeleted(false);

            return flightRepository.save(flight);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date for flight creation: " + e.getMessage());
        }
    }


    @Override
    public List<Flight> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();

        List<Flight> filteredFlights = flights.stream()
                .filter(flight -> !flight.isDeleted())
                .collect(Collectors.toList());

        if (filteredFlights.isEmpty()) {
            throw new IllegalArgumentException("No active flights found in the database. Please check the flight records or try again later.");
        }
        return filteredFlights;
    }


    @Override
    public Flight updateFlight(Long id, FlightDTO flightDTO) {
        // Obtener el vuelo actual a traves de su ID
        Optional<Flight> opFlight = flightRepository.findById(id);

        // Verificar si el vuelo existe en la bbdd
        if (opFlight.isPresent()) {
            // Obtener el vuelo desde el Optional
            Flight flight = opFlight.get();

            // Verificar si el vuelo tiene reservas existentes
            if (!flight.getFlightReservations().isEmpty()) {
                // Lanzar una except si hay reservas existentes, indicando que no se puede actualizar el vuelo
                throw new IllegalArgumentException("Unable to update the flight. It has existing reservations.");
            }

            // Actualizar los atributos del vuelo con los nuevos valores proporcionados
            flight.setArrival(flightDTO.getArrival());
            flight.setDeparture(flightDTO.getDeparture());

            // Generar un nuevo cod de vuelo basado en el nombre proporcionado
            String flightCodeGen = generateFlightCode(flightDTO.getName());

            // Actualizar otros atributos del vuelo con los valores proporcionados
            flight.setDate(flightDTO.getDate());
            flight.setArrival(flightDTO.getArrival());
            flight.setDeparture(flightDTO.getDeparture());

            // Guardar y devolver el vuelo actualizado
            return flightRepository.save(flight);
        } else {
            // Lanzar una excep si el vuelo no se encuentra en la bbdd
            throw new IllegalArgumentException("Flight with ID " + id + " not found in the database.");
        }
    }


    @Override
    public Flight getFlightById(Long id) {
        Optional<Flight> optionalFlight = flightRepository.findById(id);

        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();

            if (flight.isDeleted()) {
                throw new IllegalArgumentException("Flight with ID " + id + " is marked as deleted in the database.");
            } else {
                return flight;
            }
        } else {
            throw new IllegalArgumentException("Flight with ID " + id + " not found in the database.");
        }
    }

    @Override
    public Flight deleteFlightById(Long id) {
        Optional<Flight> optionalFlight = flightRepository.findById(id);

        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();

            if (!flight.getFlightReservations().isEmpty() && flight.getFlightReservations().stream().anyMatch(FlightReservation::isDeleted)) {
                flight.setDeleted(true);
                flight.setLastUpdate(LocalDate.now());
                return flightRepository.save(flight);
            }
            //El status del vuelo significa que el vuelo ha sido borrado
            if (flight.isDeleted()) {
                throw new IllegalArgumentException("The flight cannot be deleted because it is not found in the database.");
            }

            if (!flight.getFlightReservations().isEmpty()) {
                //Si el vuelo existe, devuelvo ese mismo vuelo
                throw new IllegalArgumentException("The flight has a reservation.");
            }

            flight.setDeleted(true);
            flight.setLastUpdate(LocalDate.now());
            return flightRepository.save(flight);
        }else {
            throw new IllegalArgumentException("The flight is not found in the database.");
        }
    }

    @Override
    public List<Flight> getAllFlightsByLocationAndDate(LocalDate avaliableDateFrom, LocalDate avaliableDateTo, String arrival, String departure) {
        List<Flight> flights = flightRepository.getAllFlightDate(avaliableDateFrom, avaliableDateTo, arrival, departure);
        if (flights.isEmpty() || flights.stream().anyMatch(flight -> flight.getFlightReservations().stream().anyMatch(FlightReservation::isDeleted))) {
            throw new IllegalArgumentException("No flights match the specified criteria in the database.");

        }
        return flights;
    }

    // Metodo para construir un nombre unico usando la ciudad de llegada y la fecha
    private String buildFlightName(String arrivalCity, LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Elimina espacios en blanco y otros caracteres no deseados
        String sanitizedArrivalCity = arrivalCity.replaceAll("\\s+", "");
        // Combina la ciudad de llegada y la fecha formateada para construir el nombre del vuelo
        return sanitizedArrivalCity + "_" + formattedDate;
    }
    // Metodo para el codigo de vuelo
    private static final int MAX_CODE_LENGTH = 2;
    private static final int RANDOM_DIGIT_COUNT = 4;

    private String generateFlightCode(String flightName) {
        if (flightName == null || flightName.isEmpty()) {
            // Manejo especial si flightName es null o vacio
            return "DEFAULT_CODE"; // O el codigo que desees en este caso
        }

        String sanitizedFlightName = flightName.replaceAll("\\s+|-", "");
        String[] words = sanitizedFlightName.split("\\s+|-");

        StringBuilder code = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                code.append(word.substring(0, Math.min(MAX_CODE_LENGTH, word.length())).toUpperCase());
            }
        }

        // Generar una secuencia de numeros aleatorios
        code.append("-");
        Random random = new Random();
        for (int i = 0; i < RANDOM_DIGIT_COUNT; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
