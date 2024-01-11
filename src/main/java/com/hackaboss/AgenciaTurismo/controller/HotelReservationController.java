package com.hackaboss.AgenciaTurismo.controller;

import com.hackaboss.AgenciaTurismo.DTO.HotelReservationDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdHotelReservationDTO;
import com.hackaboss.AgenciaTurismo.model.HotelReservation;
import com.hackaboss.AgenciaTurismo.model.Room;
import com.hackaboss.AgenciaTurismo.model.User;
import com.hackaboss.AgenciaTurismo.service.IHotelReservationService;
import com.hackaboss.AgenciaTurismo.service.IRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RestController
@RequestMapping("/agency")
public class HotelReservationController {

    private static final String TEXT_PATTERN = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+";

    private static final String ERROR_CODE = "Please enter a valid hotel ID (numbers only)";

    private static final String INVALID_PLACE_NAME = "Invalid place name";
    private static final String INVALID_ROOM_TYPE = "Invalid room type";
    private static final String INVALID_USER_NAME = "Invalid user name";
    private static final String INVALID_USER_LASTNAME = "Invalid user lastname";
    private static final String INVALID_USER_EMAIL = "Invalid user email";
    private static final String INVALID_USER_PASSPORT = "Invalid user passport";
    private static final String INVALID_USER_AGE = "Invalid user age";
    private static final String DATE_ORDER_ERROR_START = "Error: Disponibility start date must be before end date.";
    private static final String DATE_ORDER_ERROR_END = "Error: Disponibility end date must be after start date.";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSPORT_PATTERN = "^[A-Za-z0-9]{6,15}$";

    @Autowired
    private IHotelReservationService hotelReservationService;

    @Autowired
    private IRoomService roomService;

    // CREACION DE UN HOTEL RESERVA
    @Operation(summary = "Crea una reserva de un hotel",
            description = "Este metodo permite crear una reserva de un hotel. Devuelve diferentes codigos de respuesta HTTP dependiendo del resultado de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid hotel reservation data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/hotel-booking/new")
    public ResponseEntity<?> createHotelReservation(@RequestBody HotelReservationDTO hotelReservationDTO) {
// Validar la fecha y otros datos antes de intentar crear la reserva
        String validation = valitadeBookingDate(hotelReservationDTO);

        if (validation != null) {
            return ResponseEntity.badRequest().body(validation);
        }

        // Realizar verificación antes de crear la reserva
        int peopleQ = hotelReservationDTO.getHosts().size();
        String roomType = hotelReservationDTO.getRoomType();
        int maxPeopleAllowed = 0;

        if (roomType.equalsIgnoreCase("doble")) {
            maxPeopleAllowed = 2;
        } else if (roomType.equalsIgnoreCase("triple")) {
            maxPeopleAllowed = 3;
        } else if (roomType.equalsIgnoreCase("individual")) {
            maxPeopleAllowed = 1;
        } else {
            // Tipo de habitación no reconocido
            return ResponseEntity.badRequest().body("Invalid room type. Unable to complete the reservation.");
        }

        // Verificar si la cantidad de personas es mayor que la permitida
        if (peopleQ > maxPeopleAllowed) {
            return ResponseEntity.badRequest().body("The " + roomType + " room allows a maximum of " + maxPeopleAllowed + " people.");
        }
        //validamos los datos que hay dentro de la base de datos
        try {

            // Intentar crear la reserva
            hotelReservationService.createReservation(hotelReservationDTO);

            int nights = (int) ChronoUnit.DAYS.between(hotelReservationDTO.getDateFrom(), hotelReservationDTO.getDateTo());

            // Buscamos el precio de la habitación a través del hotel y comprobamos que la habitación esté en el hotel
            List<Room> rooms = roomService.getAllRooms();

            // Buscamos la habitación correspondiente al tipo y al hotel
            double totalPrice = rooms.stream()
                    .filter(room -> room.getHotel().getCodHotel().equals(hotelReservationDTO.getCodHotel()) &&
                            room.getRoomType().equalsIgnoreCase(hotelReservationDTO.getRoomType()))
                    .mapToDouble(room -> nights * room.getRoomPrice())
                    .findFirst()
                    .orElse(0.0);

            if (totalPrice == 0.0) {
                return ResponseEntity.badRequest().body("Unable to find room for the given hotel and room type.");
            }

            return ResponseEntity.ok().body("The total price of the reservation is: " + totalPrice + " €");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // BORRADO POR ID
    @Operation(summary = "Delete a hotel reservation by ID",
            description = "This method deletes a specific hotel reservation based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel reservation found with the given ID."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/hotel-booking/delete/{id}")
    public ResponseEntity<?> deleteBookHotel(@PathVariable String id) {

        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body(ERROR_CODE);
        }

        Long hotelReservationID = Long.parseLong(id);

        try {
            hotelReservationService.deleteReservation(hotelReservationID);
            return ResponseEntity.ok().body("Reservation deleted");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    // OBTENER TODAS LAS RESERVAS DE UN HOTEL

    @Operation(summary = "Obtener todas las reservas de un hotel",
            description = "Este metodo devuelve todas las reservas. Devuelve diferentes codigos de respuesta HTTP dependiendo del resultado de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all hotel reservations."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel reservations found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotel-booking/all")
    public ResponseEntity<?> getAllReservationsHotels() {
        try {
            List<HotelReservation> hotelReservations = hotelReservationService.getAllReservations();
            return ResponseEntity.ok().body(hotelReservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // OBTENER RESERVA DE UN HOTEL POR ID
    @Operation(summary = "Obtener la reserva de un hotel por su ID",
            description = "Este metodo devuelve la reserva de un hotel por su ID. Devuelve diferentes codigos de respuesta HTTP dependiendo del resultado de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation information."),
            @ApiResponse(responseCode = "400", description = "Invalid request or hotel reservation not found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotel-booking/get/{id}")
    public ResponseEntity<?> getHotelReservationId(@PathVariable String id) {
        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }
            HotelReservation hotelReservation = hotelReservationService.getReservationById(Long.parseLong(id));
            return ResponseEntity.ok().body(hotelReservation);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }
    // ACTUALIZACION DE UN HOTEL RESERVA
    @Operation(summary = "Editar la reserva de un hotel por su ID",
            description = "Este metodo permite manejar la edicion de una reserva. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion..")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid hotel reservation data or unable to find room."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/hotel-booking/edit/{id}")
    public ResponseEntity<?> editHotelReservation(@PathVariable String id, @RequestBody UpdHotelReservationDTO hotelReservationDTO) {
        try {
            if (!id.matches("^\\d+$")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }

            List<Room> rooms = roomService.getAllRooms();

            Long reservationId = Long.parseLong(id);

            // Obtengo los datos de la bbdd
            int currNights = hotelReservationService.getAllReservations().get(0).getNights();
            Double oPrice = hotelReservationService.getAllReservations().get(0).getPrice();

            HotelReservation hotelReservation = hotelReservationService.updateReservationID(reservationId, hotelReservationDTO);

            LocalDate dateFrom = hotelReservationDTO.getDateFrom();
            LocalDate dateTo = hotelReservationDTO.getDateTo();

            Period period = Period.between(dateFrom, dateTo);
            int nights = period.getDays();

            double totalPrice = rooms.stream()
                    .filter(room -> room.getHotel().getCodHotel().equals(hotelReservation.getCodHotel()) &&
                            room.getRoomType().equalsIgnoreCase(hotelReservation.getRoomType()))
                    .mapToDouble(room -> nights * room.getRoomPrice())
                    .sum();

            double updatePrice = nights < currNights ? oPrice - totalPrice :
                    nights > currNights ? totalPrice - oPrice : 0.0;

            String message = "Reservation details not available." + '\n' +
                    (nights < currNights ? "The amount of money to be returned to the client is: " :
                            nights > currNights ? "The additional cost to pay is: " : "No changes in cost.") + updatePrice;

            // Actualizacion de las noches y el precio
            hotelReservation.setNights(nights);
            hotelReservation.setPrice(totalPrice);

            return ResponseEntity.ok().body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private String valitadeBookingDate(HotelReservationDTO hotelReservationDTO) {

        if (hotelReservationDTO.getDateFrom() == null || hotelReservationDTO.getDateTo() == null) {
            return "Date data is required";
        }

        if (hotelReservationDTO.getLocation() == null || !hotelReservationDTO.getLocation().matches(TEXT_PATTERN)) {
            return INVALID_PLACE_NAME;
        }

        if (hotelReservationDTO.getRoomType() == null || !hotelReservationDTO.getRoomType().matches(TEXT_PATTERN)) {
            return INVALID_ROOM_TYPE;
        }

        if (hotelReservationDTO.getDateFrom().isAfter(hotelReservationDTO.getDateTo())) {
            return DATE_ORDER_ERROR_START;
        }

        if (hotelReservationDTO.getDateTo().isAfter(hotelReservationDTO.getDateTo())) {
            return DATE_ORDER_ERROR_END;
        }

        return valitadeBookingUser(hotelReservationDTO.getHosts());
    }

    private String getReservationDetails(HotelReservation bookHotel) {
        // Devuelvo la información específica de la reserva
        return "Reservation details: " +
                "\nHotel Code: " + bookHotel.getCodHotel() +
                "\nRoom Type: " + bookHotel.getRoomType() +
                "\nNights: " + bookHotel.getNights() +
                "\nTotal Price: " + bookHotel.getPrice();
    }

    private String valitadeBookingUser(List<User> users) {

        if (users == null || users.isEmpty()) {
            return "User information is required";
        }

        if (users.get(0).getName() == null || !users.get(0).getName().matches(TEXT_PATTERN)) {
            return INVALID_USER_NAME;
        }

        if (users.get(0).getLastName() == null || !users.get(0).getLastName().matches(TEXT_PATTERN)) {
            return INVALID_USER_LASTNAME;
        }

        if (users.get(0).getEmail() == null || !users.get(0).getEmail().matches(EMAIL_PATTERN)) {
            return INVALID_USER_EMAIL;
        }

        if (users.get(0).getPassport() == null || !users.get(0).getPassport().matches(PASSPORT_PATTERN)) {
            return INVALID_USER_PASSPORT;
        }

        if (users.get(0).getAge() <= 0) {
            return INVALID_USER_AGE;
        }
        return null;
    }

}