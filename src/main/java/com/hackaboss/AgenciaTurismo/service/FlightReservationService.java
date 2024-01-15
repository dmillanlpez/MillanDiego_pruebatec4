package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.FlightReservationDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdFlightReservationDTO;
import com.hackaboss.AgenciaTurismo.model.Flight;
import com.hackaboss.AgenciaTurismo.model.FlightReservation;
import com.hackaboss.AgenciaTurismo.model.User;
import com.hackaboss.AgenciaTurismo.repository.FlightRepository;
import com.hackaboss.AgenciaTurismo.repository.FlightReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class FlightReservationService implements IFlightReservationService {

    // Implementaciones
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightReservationRepository flightReservationRepository;

    @Autowired
    private IUserService userService;

    @Override
    public FlightReservation createFlightReservation(FlightReservationDTO flightReservationDTO) {

        // Crear una reserva de vuelo
        FlightReservation flightReservation = new FlightReservation();
        List<User> users = new ArrayList<>();

        // Obtener información sobre el vuelo en la base de datos
        Flight flight = flightRepository.findByCodFlight(flightReservationDTO.getCodFlight());

        // Validar la existencia del vuelo en la base de datos
        if (flight == null) {
            throw new IllegalArgumentException("The specified flight does not exist in the database.");
        }

        // Validar la disponibilidad
        if (!flight.getFlightReservations().isEmpty() && flight.isDeleted()) {
            throw new IllegalArgumentException("The specified flight is not available for reservations.");
        }

        // Validar la fecha del vuelo con la fecha de reserva
        if (!flight.getDate().isEqual(flightReservationDTO.getDate())) {
            throw new IllegalArgumentException("The date of the specified flight does not match the date of the reservation.");
        }

        // Verificar y crear usuarios en la base de datos
        for (User userdto : flightReservationDTO.getPassengers()) {
            User user = userService.findByEmail(userdto.getEmail());
            if (user == null) {
                user = userService.createUser(userdto);
            }

            user.setName(userdto.getName());
            user.setLastName(userdto.getLastName());
            user.setEmail(userdto.getEmail());
            user.setPassport(userdto.getPassport());
            user.setAge(userdto.getAge());

            userService.createUser(user);

            users.add(user);
        }

        // Actualizar datos del usuario

        // Verificar la fecha y realizar la reserva en el caso de que la fecha sea válida
        LocalDate currentDate = LocalDate.now();
        if (flightReservationDTO.getCodFlight().equals(flight.getCodFlight()) &&
                !flightReservationDTO.getSeatType().equals(flightReservation.getSeatType()) &&
                flightReservationDTO.getPassengers() != flightReservation.getPassengers()) {

            if (flightReservationDTO.getDate().isAfter(currentDate) || flightReservationDTO.getDate().isEqual(currentDate)) {
                // La fecha de reserva es válida

                // Configurar los datos de la reserva
                flightReservation.setCodFlight(flight.getCodFlight());
                flightReservation.setArrival(flight.getArrival());
                flightReservation.setDeparture(flight.getDeparture());
                flightReservation.setPeopleQ(users.size());
                flightReservation.setSeatType(flightReservationDTO.getSeatType());
                flightReservation.setDate(flightReservationDTO.getDate());

                double priceTotal = (users.size() * flightReservationDTO.getPrice());
                flightReservation.setPrice(priceTotal);

                flightReservation.setPassengers(users);
                flightReservation.setFlight(flight);
                flightReservation.setDeleted(false);

                // Comprobar si algún usuario ya tiene una reserva activa para la fecha del vuelo
                List<FlightReservation> activeBookings = flightReservationRepository.getUserActiveBookings(users, flightReservationDTO.getDate());

                // Verificar si algun usuario ya tiene una reserva activa para el mismo vuelo
                if (!activeBookings.isEmpty() && activeBookings.stream().anyMatch(reservation -> reservation.getFlight().getCodFlight().equals(flightReservationDTO.getCodFlight()))) {
                    throw new IllegalArgumentException("At least one user already has an active reservation for the specified flight on the specified date.");
                } else {
                    return flightReservationRepository.save(flightReservation);
                }
            } else {
                throw new IllegalArgumentException("The flight date is not valid. It must be on or after the current date.");
            }
        } else {
            throw new IllegalArgumentException("The specified flight does not exist in the database.");
        }
    }


    @Override
    public FlightReservation getFlightReservationId(Long id) {
        Optional<FlightReservation> optionalFlightReservation = flightReservationRepository.findById(id);

        // Comprobar si existe la reserva en la base de datos
        if (optionalFlightReservation.isPresent()) {
            FlightReservation flightReservation = optionalFlightReservation.get();

            // Comprobar si la reserva o el vuelo estan marcados como eliminados
            if (flightReservation.isDeleted() || flightReservation.getFlight().isDeleted()) {
                throw new IllegalArgumentException("The flight reservation with ID " + id + " is not found in the database.");
            }
            return flightReservation;
        } else {
            throw new IllegalArgumentException("The flight reservation with ID " + id + " is not found in the database.");
        }
    }


    @Override
    public List<FlightReservation> getAllFlightReservations() {
        // Obtener todas las reservas de vuelo desde el repositorio
        List<FlightReservation> flightReservations = flightReservationRepository.findAll();

        // Verificar si la lista de reservas de vuelo no está vacia
        if (!flightReservations.isEmpty()) {
            // Devolver la lista de reservas de vuelo si hay elementos
            return flightReservations;
        } else {
            // Lanzar una excepcion si no hay reservas de vuelo en la base de datos
            throw new IllegalArgumentException("There are no flight reservations in the database.");
        }
    }


    public FlightReservation updateFlightReservation(Long id, UpdFlightReservationDTO updFlightReservationDTO) {
        Optional<FlightReservation> optionalFlightReservation = flightReservationRepository.findById(id);

        if (optionalFlightReservation.isPresent()) {
            FlightReservation flightReservation = optionalFlightReservation.get();

            if (flightReservation.isDeleted()) {
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }

            // Validar que el tipo de asiento sea "Economy" o "Premium"
            String seatType = updFlightReservationDTO.getSeatType();
            if (!"Economy".equalsIgnoreCase(seatType) && !"Premium".equalsIgnoreCase(seatType)) {
                throw new IllegalArgumentException("Invalid seat type. Choose between 'Economy' and 'Premium'.");
            }

            // Automatically set the price based on seatType
            if ("Premium".equalsIgnoreCase(seatType)) {
                flightReservation.setPrice(120.0);
            } else {
                // Set price to 60 for Economy
                flightReservation.setPrice(60.0);
            }

            flightReservation.setSeatType(seatType);

            return flightReservationRepository.save(flightReservation);
        } else {
            throw new IllegalArgumentException("The reservation does not exist");
        }
    }


    @Override
    public FlightReservation deleteFlightReservation(Long id) {

        // Verificar si la reserva existe en la base de datos
        Optional<FlightReservation> optionalFlightReservation = flightReservationRepository.findById(id);

        if (optionalFlightReservation.isPresent()) {
            FlightReservation flightReservation = optionalFlightReservation.get();

            if (flightReservation.isDeleted()) {
                throw new IllegalArgumentException("The flight reservation with ID " + id + " is not found in the database.");
            }

            // Cambiar el estodo y la fecha del borrado
            flightReservation.setDeleted(true);
            flightReservation.setLastUpdate(LocalDate.now());

            return flightReservationRepository.save(flightReservation);
        } else {
            throw new IllegalArgumentException("The flight reservation with ID " + id + " is not found in the database.");
        }
    }


}