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
        // Inicializar BookHotel
        HotelReservation hotelReservation = new HotelReservation();

        // Obtener el hotel existente
        Hotel hotelExist = hotelRepository.findHotelByCodHotel(hotelReservationDTO.getCodHotel());

        if (hotelExist == null) {
            throw new IllegalArgumentException("The hotel does not exist in the database");
        }

        //Comprobamos que el hotel esté eliminado de la forma lógica

        if (hotelExist.isDeleted()) {
            throw new IllegalArgumentException("The hotel does not exist in the database");
        }
        //Comprobamos que el hotel tenga reserva
        if (!hotelExist.getLocation().equalsIgnoreCase(hotelReservationDTO.getLocation())) {
            throw new IllegalArgumentException("the hotel does not correspond to the city");

        }
        // Obtener datos
        List<Room> rooms = roomRepository.findAll();

        // Verificar la fecha
        LocalDate dateFrom = hotelReservationDTO.getDateFrom();
        LocalDate dateTo = hotelReservationDTO.getDateTo();

        // Obtenemos las fechas de la habitación específica para el tipo y código de hotel
        Room roomDisponibility = rooms.stream()
                .filter(r -> r.getHotel().getCodHotel().equals(hotelReservationDTO.getCodHotel()) &&
                        r.getRoomType().equalsIgnoreCase(hotelReservationDTO.getRoomType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The specific room for the reservation was not found."));

        // Se verifica que la fecha este bien, en el caso de que este correcto se inserta en la bbdd

        LocalDate startDate = roomDisponibility.getAvaliableDateFrom();
        LocalDate endDate = roomDisponibility.getAvaliableDateTo();


        // Verificamos las fechas de reserva contra las fechas de disponibilidad específicas de la habitación
        if (dateFrom.isBefore(startDate) || dateTo.isAfter(endDate)) {
            throw new IllegalArgumentException("The reservation dates are not within the room's availability range or are invalid.");
        } else {
            // Verificar si el hotel no está reservado
            if (!hotelExist.isBooked()) {
                hotelRepository.save(hotelExist);

                hotelReservation.setCodHotel(hotelExist.getCodHotel());
                hotelReservation.setDateFrom(dateFrom);
                hotelReservation.setDateTo(dateTo);

                // Calcular las noches con las fechas que ingresamos
                int nights = (int) ChronoUnit.DAYS.between(dateFrom, dateTo);
                hotelReservation.setNights(nights);

                // Crear la lista de usuarios
                List<User> users = new ArrayList<>();

                for (User userDto : hotelReservationDTO.getHosts()) {
                    User user = userService.findByEmail(userDto.getEmail());
                    if (user == null) {
                        // Si el usuario no existe, crea uno nuevo
                        user = new User();
                    }
                    // Actualizar datos del usuario
                    user.setName(userDto.getName());
                    user.setLastName(userDto.getLastName());
                    user.setEmail(userDto.getEmail());
                    user.setPassport(userDto.getPassport());
                    user.setAge(userDto.getAge());

                    // Guardar o actualizar el usuario
                    userService.createUser(user);

                    users.add(user);
                }

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

                //Comprobamos que el usuario no pueda reservar dos habitaciones distintas con la misma fecha
                List<HotelReservation> existingBookings = hotelReservationRepository.getUserAndDateRange(users, dateFrom, dateTo);

                // Comprobamos si alguna de las reservas existentes coincide con las fechas de la nueva reserva
                if (existingBookings.isEmpty() && existingBookings.stream().noneMatch(HotelReservation::isDeleted)) {
                    hotelExist.setBooked(true);
                    return hotelReservationRepository.save(hotelReservation);
                } else {
                    throw new IllegalArgumentException("The user already has a reservation for the specified dates.");
                }


            } else {
                throw new IllegalArgumentException("The hotel is already booked");
            }
        }
    }

    @Override
    public List <HotelReservation> getAllReservations() {

        List<HotelReservation> hotelReservations = hotelReservationRepository.findAll();
        List<HotelReservation> filterHotelReservations = new ArrayList<>();

        if (hotelReservations.stream().anyMatch(hotelReservation -> !hotelReservation.isDeleted())) {
            for (HotelReservation hotelReservation : hotelReservations) {
                if (!hotelReservation.isDeleted()) {
                    filterHotelReservations.add(hotelReservation);
                }
            }
            return filterHotelReservations;
        }else {
            throw new IllegalArgumentException("There are no available booked hotels in the database.");
        }

    }


    @Override
    public HotelReservation updateReservationID(Long id, UpdHotelReservationDTO updHotelReservationDTO) {

        Optional<HotelReservation> optionalHotelReservationhotelReservation = hotelReservationRepository.findById(id);

        //Hacermos lo mismo que el eliminado pero en vez de eliminarlo para actualizarlo
        if (optionalHotelReservationhotelReservation.isPresent()) {
            HotelReservation hotelReservation = getHotelReservation(updHotelReservationDTO, optionalHotelReservationhotelReservation);

            hotelReservation.setDateFrom(updHotelReservationDTO.getDateFrom());
            hotelReservation.setDateTo(updHotelReservationDTO.getDateTo());


            //Al cambiar las fechas entonces tendremos que cambiar tanto las noches y
            // evidentemente también el precio

            int nights = (int) ChronoUnit.DAYS.between(updHotelReservationDTO.getDateFrom(), updHotelReservationDTO.getDateTo());
            hotelReservation.setNights(nights);

            //Buscamos la reserva
            List<Room> rooms = roomRepository.findAll();

            //Aquí obtenemos el precio total de la habitación
            double totalPrice = rooms.stream()
                    .filter(room -> room.getHotel().getCodHotel().equals(hotelReservation.getCodHotel()) &&
                            room.getRoomType().equalsIgnoreCase(hotelReservation.getRoomType()))
                    .mapToDouble(room -> nights * room.getRoomPrice())
                    .findFirst()
                    .orElse(0.0);
            hotelReservation.setPrice(totalPrice);


            return hotelReservationRepository.save(hotelReservation);
        }else {
            throw new IllegalArgumentException("The hotel reservation does not exist");
        }
    }

        @Override
        public HotelReservation deleteReservation (Long id){

            // Primero se verifica que la reserva exista en la base de datos
            Optional<HotelReservation> opHotelReservation = hotelReservationRepository.findById(id);

            // Se verifica que exista, y en el caso de que esta exista que sea eliminada
            if (opHotelReservation.isPresent()) {
                HotelReservation hotelReservation = opHotelReservation.get();

                if (hotelReservation.isDeleted()) {
                    throw new IllegalArgumentException("The reservation is not found in the database.");
                }

                hotelReservation.setDeleted(true);
                hotelReservation.setUpdated(LocalDate.now());
                return hotelReservationRepository.save(hotelReservation);
            } else {
                throw new IllegalArgumentException("The hotel reservation that you want to delete does not exist in the database.");
            }
        }

        @Override
        public HotelReservation getReservationById (Long id){

            Optional<HotelReservation> opHotelReservation = hotelReservationRepository.findById(id);

            // Se verifica que existe la reserva en la base de datos
            if (opHotelReservation.isPresent()) {
                HotelReservation hotelReservation = opHotelReservation.get();

                if (hotelReservation.isDeleted()) {
                    throw new IllegalArgumentException("The reservation is not found in the database.");
                }
                return hotelReservation;
            } else {
                throw new IllegalArgumentException("The hotel is not found in our database.");
            }
        }

    //Método para validar la fecha de la reserva y devolver un detalle de la actualización de la reserva del hotel
    private static HotelReservation getHotelReservation(UpdHotelReservationDTO updHotelReservationDTO, Optional<HotelReservation> optionalHotelReservation) {
        HotelReservation hotelReservation = optionalHotelReservation.get();

        //verificamos las fechas si están dentro del rango de la habitación
        if (updHotelReservationDTO.getDateFrom().isBefore(hotelReservation.getHotel().getRoom().getAvaliableDateFrom()) ||
                updHotelReservationDTO.getDateTo().isAfter(hotelReservation.getHotel().getRoom().getAvaliableDateTo()) ||
                updHotelReservationDTO.getDateTo().isBefore(hotelReservation.getHotel().getRoom().getAvaliableDateFrom())||
                updHotelReservationDTO.getDateTo().isBefore(updHotelReservationDTO.getDateFrom())) {
            throw new IllegalArgumentException("The reservation dates are not within the room's availability range or are not valid." +
                    " The dates are:\n" +
                    //Obtenemos las fechas de la habitación para mostrarle al usuario el rango de fechas que tiene la habitación
                    "- From: " + updHotelReservationDTO.getDateFrom() +'\n' +
                    "- To: " + updHotelReservationDTO.getDateTo()  + '\n' +
                    "The dates you want to change are:\n" +
                    "- From: " + hotelReservation.getDateFrom() + '\n' +
                    "- To: " + hotelReservation.getDateTo() + '\n' );
        }

        if (hotelReservation.isDeleted()) {
            throw new IllegalArgumentException("The reservation is not found in the database.");
        }
        return hotelReservation;
    }
}



