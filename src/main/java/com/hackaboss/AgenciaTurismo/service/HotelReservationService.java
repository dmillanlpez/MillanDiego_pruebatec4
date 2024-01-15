package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.HotelReservationDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdHotelReservationDTO;
import com.hackaboss.AgenciaTurismo.model.Hotel;
import com.hackaboss.AgenciaTurismo.model.HotelReservation;
import com.hackaboss.AgenciaTurismo.model.Room;
import com.hackaboss.AgenciaTurismo.model.User;
import com.hackaboss.AgenciaTurismo.repository.HotelRepository;
import com.hackaboss.AgenciaTurismo.repository.HotelReservationRepository;
import com.hackaboss.AgenciaTurismo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelReservationService implements IHotelReservationService {

    // Inyeccion de dependencia de los repositorios de habitacion, hotel, hotelReservation y user

    @Autowired
    private HotelReservationRepository hotelReservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private IUserService userService;

    @Override
    public HotelReservation createReservation(HotelReservationDTO hotelReservationDTO) {
        // Verificar si el hotel existe
        Hotel hotelExist = hotelRepository.findHotelByCodHotel(hotelReservationDTO.getCodHotel());

        if (hotelExist == null || hotelExist.isDeleted()) {
            throw new IllegalArgumentException("The specified hotel does not exist or has been deleted.");
        }

        // Verificar que el hotel corresponda a la ciudad
        if (!hotelExist.getLocation().equalsIgnoreCase(hotelReservationDTO.getLocation())) {
            throw new IllegalArgumentException("The specified hotel does not correspond to the provided location.");
        }

        // Obtener datos
        List<Room> rooms = roomRepository.findAll();

        // Verificar la fecha
        LocalDate dateFrom = hotelReservationDTO.getDateFrom();
        LocalDate dateTo = hotelReservationDTO.getDateTo();

        // Obtener la habitación especifica para el tipo y código de hotel
        Room roomDisponibility = rooms.stream()
                .filter(r -> r.getHotel().getCodHotel().equals(hotelReservationDTO.getCodHotel()) &&
                        r.getRoomType().equalsIgnoreCase(hotelReservationDTO.getRoomType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The specified room for the reservation was not found."));

        // Verificar las fechas de reserva contra las fechas de disponibilidad especificas de la habitacion
        LocalDate startDate = roomDisponibility.getAvaliableDateFrom();
        LocalDate endDate = roomDisponibility.getAvaliableDateTo();

        if (dateFrom.isBefore(startDate) || dateTo.isAfter(endDate)) {
            throw new IllegalArgumentException("The reservation dates are not within the room's availability range or are invalid.");
        }

        // Verificar si el hotel no se encuentra reservado
        if (!hotelExist.isBooked()) {
            // Guardar el estado del hotel como reservado
            hotelExist.setBooked(true);
            hotelRepository.save(hotelExist);

            // Crear la reserva
            HotelReservation hotelReservation = new HotelReservation();
            hotelReservation.setCodHotel(hotelExist.getCodHotel());
            hotelReservation.setDateFrom(dateFrom);
            hotelReservation.setDateTo(dateTo);

            // Calcular las noches con las fechas proporcionadas
            int nights = (int) ChronoUnit.DAYS.between(dateFrom, dateTo);
            hotelReservation.setNights(nights);

            // Crear la lista de usuarios
            List<User> users = hotelReservationDTO.getHosts().stream()
                    .map(userDto -> {
                        User user = userService.findByEmail(userDto.getEmail());
                        if (user == null) {
                            user = new User();
                        }
                        user.setName(userDto.getName());
                        user.setLastName(userDto.getLastName());
                        user.setEmail(userDto.getEmail());
                        user.setPassport(userDto.getPassport());
                        user.setAge(userDto.getAge());
                        userService.createUser(user);
                        return user;
                    })
                    .collect(Collectors.toList());

            // Asociar el hotel y usuarios con la reserva
            hotelReservation.setRoomType(hotelReservationDTO.getRoomType());
            hotelReservation.setHotel(hotelExist);
            hotelReservation.setUsers(users);
            hotelReservation.setPeopleQ(users.size());

            // Calcular el precio por noche y total
            double totalPrice = rooms.stream()
                    .filter(room -> room.getHotel().getCodHotel().equals(hotelReservationDTO.getCodHotel()) &&
                            room.getRoomType().equalsIgnoreCase(hotelReservationDTO.getRoomType()))
                    .mapToDouble(room -> nights * room.getRoomPrice())
                    .findFirst()
                    .orElse(0.0);
            hotelReservation.setPrice(totalPrice);

            // Comprobar si el usuario ya tiene una reserva para las fechas especificadas
            List<HotelReservation> existingBookings = hotelReservationRepository.getUserAndDateRange(users, dateFrom, dateTo);

            if (existingBookings.isEmpty() && existingBookings.stream().noneMatch(HotelReservation::isDeleted)) {
                return hotelReservationRepository.save(hotelReservation);
            } else {
                throw new IllegalArgumentException("The user already has a reservation for the specified dates.");
            }
        } else {
            throw new IllegalArgumentException("The hotel is already booked.");
        }
    }


    @Override
    public List<HotelReservation> getAllReservations() {

        List<HotelReservation> hotelReservations = hotelReservationRepository.findAll();
        List<HotelReservation> filterHotelReservations = new ArrayList<>();

        if (hotelReservations.stream().anyMatch(hotelReservation -> !hotelReservation.isDeleted())) {
            for (HotelReservation hotelReservation : hotelReservations) {
                if (!hotelReservation.isDeleted()) {
                    filterHotelReservations.add(hotelReservation);
                }
            }
            return filterHotelReservations;
        } else {
            throw new IllegalArgumentException("There are no available booked hotels in the database.");
        }
    }


    @Override
    public HotelReservation updateReservationID(Long id, UpdHotelReservationDTO updHotelReservationDTO) {
        HotelReservation hotelReservation = hotelReservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel reservation with ID " + id + " does not exist."));

        // Actualizar los campos necesarios
        hotelReservation.setDateFrom(updHotelReservationDTO.getDateFrom());
        hotelReservation.setDateTo(updHotelReservationDTO.getDateTo());

        // Actualizar las noches y el precio segun las nuevas fechas
        int nights = (int) ChronoUnit.DAYS.between(updHotelReservationDTO.getDateFrom(), updHotelReservationDTO.getDateTo());
        hotelReservation.setNights(nights);

        List<Room> rooms = roomRepository.findAll();
        double totalPrice = rooms.stream()
                .filter(room -> room.getHotel().getCodHotel().equals(hotelReservation.getCodHotel()) &&
                        room.getRoomType().equalsIgnoreCase(hotelReservation.getRoomType()))
                .mapToDouble(room -> nights * room.getRoomPrice())
                .findFirst()
                .orElse(0.0);
        hotelReservation.setPrice(totalPrice);

        // Guardar la reserva actualizada
        return hotelReservationRepository.save(hotelReservation);
    }


    @Override
    public HotelReservation deleteReservation(Long id) {

        // Primero se verifica que la reserva exista en la base de datos
        Optional<HotelReservation> opHotelReservation = hotelReservationRepository.findById(id);

        // Se verifica que exista, y en el caso de que esta exista que sea eliminada
        if (opHotelReservation.isPresent()) {
            HotelReservation hotelReservation = opHotelReservation.get();

            if (hotelReservation.isDeleted()) {
                throw new IllegalArgumentException("The reservation with ID " + id + " is already deleted.");
            }

            hotelReservation.setDeleted(true);
            hotelReservation.setUpdated(LocalDate.now());
            return hotelReservationRepository.save(hotelReservation);
        } else {
            throw new IllegalArgumentException("The reservation with ID " + id + " does not exist in the database.");
        }
    }

    @Override
    public HotelReservation getReservationById(Long id) {

        Optional<HotelReservation> opHotelReservation = hotelReservationRepository.findById(id);

        // Se verifica que existe la reserva en la base de datos
        if (opHotelReservation.isPresent()) {
            HotelReservation hotelReservation = opHotelReservation.get();

            if (hotelReservation.isDeleted()) {
                throw new IllegalArgumentException("The reservation with ID " + id + " is not found in the database.");
            }
            return hotelReservation;
        } else {
            throw new IllegalArgumentException("The reservation with ID " + id + " does not exist in our database.");
        }
    }
}




