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
                throw new IllegalArgumentException("The date you are trying to create a flight is in the past.");
            }

            // Creacion de un vuelo
            Flight flight = new Flight();

            flight.setArrival(flightDTO.getArrival());
            flight.setDeparture(flightDTO.getDeparture());

            // Construir un nombre único usando la ciudad de llegada y la fecha
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
            throw new IllegalArgumentException("There are no available flights in the database.");
        }

        return filteredFlights;
    }


    @Override
    public Flight updateFlight(Long id, FlightDTO flightDTO) {

        Optional<Flight> opFlight = flightRepository.findById(id);

        if(opFlight.isPresent()){
            Flight flight = opFlight.get();

            if (!flight.getFlightReservations().isEmpty()) {
                throw new IllegalArgumentException("The flight cannot be updated because it has a reservation.");
            }

            flight.setArrival(flightDTO.getArrival());
            flight.setDeparture(flightDTO.getDeparture());

            String flightCodeGen = generateFlightCode(flightDTO.getName());

            flight.setDate(flightDTO.getDate());
            flight.setArrival(flightDTO.getArrival());
            flight.setDeparture(flightDTO.getDeparture());

            return flightRepository.save(flight);
        }else {
            throw new IllegalArgumentException("The flight was not found in the database.");
        }

    }

    @Override
    public Flight getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The flight is not found in the database."));

        if (!flight.getFlightReservations().isEmpty() && flight.getFlightReservations().stream().anyMatch(FlightReservation::isDeleted)) {
            flight.setDeleted(true);
            flight.setLastUpdate(LocalDate.now());
            return flightRepository.save(flight);
        }

        if (flight.isDeleted()) {
            throw new IllegalArgumentException("The flight cannot be deleted because it is not found in the database.");
        }

        if (!flight.getFlightReservations().isEmpty()) {
            throw new IllegalArgumentException("The flight has a reservation.");
        }

        flight.setDeleted(true);
        flight.setLastUpdate(LocalDate.now());
        return flightRepository.save(flight);
    }
    @Override
    public Flight deleteFlightById(Long id) {

        Optional<Flight> opFlight = flightRepository.findById(id);

        if(opFlight.isPresent()){
            Flight flight = opFlight.get();

            if(!flight.getFlightReservations().isEmpty() && flight.getFlightReservations().stream().anyMatch(FlightReservation::isDeleted)) {
                flight.setDeleted(true);
                flight.setLastUpdate(LocalDate.now());
                return flightRepository.save(flight);
            }

            if(flight.isDeleted()){
                throw new IllegalArgumentException("The flight cannot be deleted because it is not found in the database.");
            }

            if(!flight.getFlightReservations().isEmpty()) {
                throw new IllegalArgumentException("The flight has a reservation.");
            }

            flight.setDeleted(true);
            flight.setLastUpdate(LocalDate.now());
            return flightRepository.save(flight);
        }else{
            throw new IllegalArgumentException("The flight is not found in the database.");
        }

    }

    @Override
    public List<Flight> getAllFlightsByLocationAndDate(LocalDate avaliableDateFrom, LocalDate avaliableDateTo, String arrival, String departure) {
        List<Flight> flights = flightRepository.getAllFlightDate(avaliableDateFrom, avaliableDateTo, arrival, departure);
        if (flights.isEmpty() || flights.stream().anyMatch(flight -> flight.getFlightReservations().stream().anyMatch(FlightReservation::isDeleted))) {
            throw new IllegalArgumentException("There are no flights with these criteria in the database.");

        }
        return flights;
    }

    // Método para construir un nombre único usando la ciudad de llegada y la fecha
    private String buildFlightName(String arrivalCity, LocalDate date) {
        // Puedes personalizar este formato según tus preferencias
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return arrivalCity.replaceAll("\\s+", "") + "_" + formattedDate;
    }

    private String generateFlightCode(String flightName) {
        if (flightName == null) {
            // Manejo especial si flightName es null
            return "DEFAULT_CODE"; // O el código que desees en este caso
        }

        String[] words = flightName.split("\\s+|-");

        StringBuilder code = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                code.append(word.substring(0, Math.min(2, word.length())).toUpperCase());
            }
        }

        // Generar una secuencia de números aleatorios
        Random random = new Random();
        code.append("-");
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }
}
