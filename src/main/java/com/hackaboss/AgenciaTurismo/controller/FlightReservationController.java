package com.hackaboss.AgenciaTurismo.controller;

import com.hackaboss.AgenciaTurismo.DTO.FlightReservationDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdFlightReservationDTO;
import com.hackaboss.AgenciaTurismo.model.FlightReservation;
import com.hackaboss.AgenciaTurismo.model.User;
import com.hackaboss.AgenciaTurismo.service.IFlightReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("agency")
public class FlightReservationController {

    // Validaciones
    private static final String TEXT_PATTERN = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+";
    private static final String ERROR_CODE = "Please enter a valid hotel ID (numbers only)";

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSPORT_PATTERN = "^[A-Za-z0-9]{6,15}$";
    private static final String INVALID_USER_NAME = "Invalid user name";
    private static final String INVALID_USER_LASTNAME = "Invalid user lastname";
    private static final String INVALID_USER_EMAIL = "Invalid user email";
    private static final String INVALID_USER_PASSPORT = "Invalid user passport";
    private static final String INVALID_USER_AGE = "Invalid user age";
    private static final String ERROR_ORIGIN_DESTINATION = "correctly write the origin and/or destination of flight.";
    private static final String ERROR_COINCIDENCE_FLIGHTS = "the origin and destination do not match the flight";
    private static final String DATE_ERROR = "Error: Disponibility start date must be before end date.";
    private static final String ERROR_CODE_NULL = "Flight code is required";
    private static final String ERROR_SEATTYPE_ERROR = "Error: Seat type does not match.";

    @Autowired
    private IFlightReservationService flightReservationService;


    @Operation(summary = "Crea una reserva de vuelo",
            description = "Este metodo maneja la creacion de una reserva de vuelo. Devuelve diferentes codigos de respuesta HTTP dependiendo del resultado de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid book flight data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/flight-booking/new")
    public ResponseEntity<?> createBookFlight(@RequestBody FlightReservationDTO flightReservationDTO) {
        try {
            String validationResult = validator(flightReservationDTO);
            if (validationResult != null) {
                return ResponseEntity.badRequest().body(validationResult);
            }

            flightReservationService.createFlightReservation(flightReservationDTO);

            Double totalPrice = flightReservationDTO.getPrice() * flightReservationDTO.getPassengers().size();

            return ResponseEntity.ok().body("The total price of the flight is: " + totalPrice);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener todas las reservas de vuelo",
            description = "Este metodo se encarga de obtener todas las reservas de vuelo. Devuelve diferentes codigos de respuesta HTTP dependiendo del resultado de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all flight reservations."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight reservations found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flight-booking/all")
    public ResponseEntity<?> getAllBookFlights() {
        try{
            List<FlightReservation> flightReservations = flightReservationService.getAllFlightReservations();
            return ResponseEntity.ok().body(flightReservations);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(summary = "Editar una reserva de vuelo",
            description = "Este método maneja la modificación de una reserva de vuelo. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid flight data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/flight-booking/edit/{id}")
    public ResponseEntity<?> editFlightReservation(@PathVariable String id, @RequestBody UpdFlightReservationDTO updFlightReservationDTO) {
        try {
            if (!id.matches("^\\d+$")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }

            Long reservationFlightId = Long.valueOf(id);

            FlightReservation flightReservation = flightReservationService.updateFlightReservation(reservationFlightId, updFlightReservationDTO);

            if (flightReservation == null) {
                return ResponseEntity.badRequest().body(ERROR_CODE_NULL);
            }

            // Calculo del precio usando el objeto flightReservation actualizado
            Double totalPrice = flightReservationService.getAllFlightReservations().get(0).getPrice();
            return ResponseEntity.ok().body("The total price of the flight is: " + totalPrice);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(summary = "Borra una reserva de un vuelo mediante un ID",
            description = "Este metodo maneja la eliminacion de una reserva de vuelo. Devuelve diferentes codigos de respuesta HTTP dependiendo del resultado de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight reservation found with the given ID."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/flight-booking/delete/{id}")
    public ResponseEntity<?> deleteBookFlight(@PathVariable String id) {

        if (!id.matches("^\\d+$")){
            return ResponseEntity.badRequest().body(ERROR_CODE);
        }

        Long flightId = Long.valueOf(id);

        try{
            flightReservationService.deleteFlightReservation(flightId);
            return ResponseEntity.ok().body("Flight reservation deleted successfully.");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener un vuelo por su ID",
            description = "Este metodo permite la busqueda de un vuelo por su ID. Devuelve diferentes codigos de respuesta HTTP dependiendo del resultado de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation found successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight reservation found with the given ID."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flight-booking/get/{id}")
    public ResponseEntity<?> getBookFlight(@PathVariable String id) {
        try {
            if (!id.matches("^\\d+$")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }
            Long bookingFlightId = Long.valueOf(id);

            FlightReservation bookFlight = flightReservationService.getFlightReservationId(bookingFlightId);

            return ResponseEntity.ok().body(bookFlight);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    public String validator(FlightReservationDTO flightReservationDTO) {
        if (flightReservationDTO.getDate() == null) {
            return "Date data is required";
        }

        if (flightReservationDTO.getCodFlight() == null) {
            return ERROR_CODE_NULL;
        }

        String seatType = flightReservationDTO.getSeatType();

        if (!seatType.matches(TEXT_PATTERN)) {
            return ERROR_SEATTYPE_ERROR;
        }

        double price = flightReservationDTO.getPrice();
        if (price <= 0) {
            return "Price cannot be negative or 0";
        }
        return validatorUserFlight(flightReservationDTO.getPassengers());
    }

    private String validatorUserFlight(List<User> users) {
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
