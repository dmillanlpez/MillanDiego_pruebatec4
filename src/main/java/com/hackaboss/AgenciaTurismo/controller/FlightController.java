package com.hackaboss.AgenciaTurismo.controller;

import com.hackaboss.AgenciaTurismo.DTO.FlightDTO;
import com.hackaboss.AgenciaTurismo.model.Flight;
import com.hackaboss.AgenciaTurismo.service.IFlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class FlightController {

    @Autowired
    private IFlightService flightService;

    @Operation(summary = "Anadir un nuevo vuelo.",
            description = "Este metodo permite la creacion de un vuelo. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid flight data or the flight creation date cannot be in the past."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/flight/new")
    public ResponseEntity < ? > createFlight(@RequestBody FlightDTO flightDto) {
        try {
            String arrival = flightDto.getArrival();
            String departure = flightDto.getDeparture();

            if (arrival == null || departure == null ||
                    !arrival.matches("[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+") ||
                    !departure.matches("[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+")) {
                return ResponseEntity.badRequest().body("Flight origin and destination can only contain letters and cannot be empty");
            }

            Flight flight = flightService.createFlight(flightDto);
            if (flight == null) {
                return ResponseEntity.badRequest().body("The flight creation date cannot be in the past");
            }
            return ResponseEntity.ok().body("Successfully created flight");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body("Arrival or departure data is missing");
        }
    }

    @Operation(summary = "Devolver todos los vuelos existentes en la base de datos.",
            description = "Este metodo permite devolver todos los vuelos existentes en la base de datos. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all flights."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flights found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/flights")
    public ResponseEntity < ? > getAllFlights() {

        try {
            List < Flight > flights = flightService.getAllFlights();
            return ResponseEntity.ok().body(flights);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Encontrar un vuelo por su ID",
            description = "Este metodo retorna un vuelo por su ID, Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight information."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/flights/{id}")
    public ResponseEntity < ? > getFlightById(@PathVariable String id) {
        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body("Please enter a valid hotel ID (numbers only)");
            }
            Long flightID = Long.valueOf(id);

            Flight flight = flightService.getFlightById(flightID);

            return ResponseEntity.ok().body(flight);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Borrar un vuelo por su ID",
            description = "Este metodo permite borrar un vuelo por su ID, Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/flights/{id}")
    public ResponseEntity < ? > deleteFlightById(@PathVariable String id) {

        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body("Please enter a valid flight ID (numbers only)");
            }
            Long flightIdDelete = Long.valueOf(id);

            flightService.deleteFlightById(flightIdDelete);
            return ResponseEntity.ok().body("Flight deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "Obtener vuelos por la localizacion y fecha de disponibilidad. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.",
            description = "TEste metodo permite obtener vuelos por una fecha y localizacion determinada. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all flights."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flights found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/flights/search")
    public ResponseEntity < ? > getAllFlightsByPlaceAndDates(@RequestParam LocalDate avaliableDateFrom,
                                                             @RequestParam LocalDate avaliableDateTo,
                                                             @RequestParam String arrival,
                                                             @RequestParam String departure) {
        try {
            List < Flight > flights = flightService.getAllFlightsByLocationAndDate(avaliableDateFrom, avaliableDateTo, arrival, departure);
            return ResponseEntity.ok().body(flights);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Edita un vuelo por su ID",
            description = "Este metodo permite editar un vuelo por su ID. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/flights/edit/{id}")
    public ResponseEntity < ? > editFlightById(@PathVariable String id, @RequestBody FlightDTO flightDTO) {

        try {
            if (!id.matches("^\\d+$")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }
            Long flightID = Long.valueOf(id);

            // Resto del codigo para poder editar el hotel
            flightService.updateFlight(flightID, flightDTO);
            return ResponseEntity.ok().body("Hotel edited");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Errors
    private static final String ERROR_CODE = "Please enter a valid hotel ID || Use only numbers please.";
}