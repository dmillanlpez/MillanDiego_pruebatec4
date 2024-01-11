package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.FlightReservationDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdFlightReservationDTO;
import com.hackaboss.AgenciaTurismo.model.Flight;
import com.hackaboss.AgenciaTurismo.model.FlightReservation;
import com.hackaboss.AgenciaTurismo.model.User;
import com.hackaboss.AgenciaTurismo.repository.FlightRepository;
import com.hackaboss.AgenciaTurismo.repository.FlightReservationRepository;
import com.hackaboss.AgenciaTurismo.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Override
    public Flight createFlightReservation(FlightReservationDTO reservation) {

        FlightReservation flightReservation = new FlightReservation();
        List<User> users = new ArrayList<>();

        Flight flight = flightRepository.findByCodFlight(reservation.getCodFlight());

        if(flight == null){
            throw new IllegalArgumentException("The flight does not exist in the database");
        }

        if(!flight.getFlightReservations().isEmpty() && flight.isDeleted()){
            throw new IllegalArgumentException("The flight has already been booked");
        }

        if(!flight.getDate().isEqual(reservation.getDate())){
            throw new IllegalArgumentException("The flight has already been booked");
        }

        // Se verifica que exista un usuario en la base de datos, en el caso de que este no exista se crea uno
        for(User userdto : reservation.getPassengers()){
            User user = userRepository.findByEmail(userdto.getEmail());
            if(user == null){
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

        // Update datos user

        // Se verifica que la fecha este bien, en el caso de que este correcto se inserta en la bbdd
        LocalDate currentDate = LocalDate.now();

        if(reservation.getDate().isAfter(currentDate) || reservation.getDate().isBefore(currentDate)){

            // fecha ok
            flightReservation.setCodFlight(flight.getCodFlight());
            flightReservation.setArrival(flight.getArrival());
            flightReservation.setDeparture(flight.getDeparture());
            flightReservation.setPeopleQ(users.size());
            flightReservation.setSeatType(reservation.getSeatType());
            flightReservation.setDate(reservation.getDate());

            // calculo precio
            double priceFlight = (users.size() * reservation.getPrice());

            flightReservation.setPrice(priceFlight);
            flightReservation.setPassengers(users);
            flightReservation.setFlight(flight);
            flightReservation.setDeleted(false);

            // Comprobacion si algun usuario ya tiene alguna reserva para la fecha del vuelo

            List<FlightReservation> activeFlightReservations = flightReservationRepository.getUserActiveBookings(users, reservation.getDate());

            if(!activeFlightReservations.isEmpty()){
                throw new IllegalArgumentException("The flight has already been booked");
            }else{
                return flightReservationRepository.save(flightReservation).getFlight();
            }
        }else{
            throw new IllegalArgumentException("The flight date is not within the range");
        }
    }

    @Override
    public FlightReservation getFlightReservationId(Long id) {

        Optional<FlightReservation> optionalFlightReservation = flightReservationRepository.findById(id);

        // Se compruba si existe la reserva en la base de datos
        if(optionalFlightReservation.isPresent()){
            FlightReservation flightReservation = optionalFlightReservation.get();

            if(flightReservation.getFlight().isDeleted()){
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }
        }else{
            throw new IllegalArgumentException("The reservation is not found in the database.");

        }
        return flightReservationRepository.findById(id).orElse(null);
    }

    @Override
    public List<FlightReservation> getAllFlightReservations() {
        List<FlightReservation> flightReservations = flightReservationRepository.findAll();

        if (!flightReservations.isEmpty()) {
            return flightReservations;
        } else {
            throw new IllegalArgumentException("There are no flight reservations in the database.");
        }
    }


    @Override
    public FlightReservation updateFlightReservation(Long id, UpdFlightReservationDTO updFlightReservationDTO) {
        Optional<FlightReservation> optionalFlightReservation = flightReservationRepository.findById(id);

        if (optionalFlightReservation.isPresent()) {
            FlightReservation flightReservation = optionalFlightReservation.get();

            if (flightReservation.isDeleted()) {
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }

            // Validar que el precio no sea negativo ni igual a cero
            Double updatedPrice = updFlightReservationDTO.getPrice();
            if (updatedPrice == null || updatedPrice <= 0) {
                throw new IllegalArgumentException("The price must be a positive value.");
            }

            flightReservation.setSeatType(updFlightReservationDTO.getSeatType());
            flightReservation.setPrice(updatedPrice);

            return flightReservationRepository.save(flightReservation);
        } else {
            throw new IllegalArgumentException("The reservation does not exist");
        }
    }


    @Override
    public FlightReservation deleteFlightReservation(Long id) {

        // Verifico si la reserva existe en la base de datos
        Optional<FlightReservation> optionalFlightReservation = flightReservationRepository.findById(id);

        if(optionalFlightReservation.isPresent()){
            FlightReservation flightReservation = optionalFlightReservation.get();

            if(flightReservation.isDeleted()){
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }

            // cambiar estado y la fecha del borrado
            flightReservation.setDeleted(true);
            flightReservation.setLastUpdate(LocalDate.now());

            return flightReservationRepository.save(flightReservation);
        }else {
            throw new IllegalArgumentException("The reservation is not found in the database.");
        }
    }
}
