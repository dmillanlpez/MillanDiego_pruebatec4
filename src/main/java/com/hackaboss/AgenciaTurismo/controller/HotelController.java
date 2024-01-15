package com.hackaboss.AgenciaTurismo.controller;

import com.hackaboss.AgenciaTurismo.DTO.HotelDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdHotelDTO;
import com.hackaboss.AgenciaTurismo.model.Hotel;
import com.hackaboss.AgenciaTurismo.service.IHotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    // CREAR UN NUEVO HOTEL
    @Operation(summary = "Anadir un nuevo hotel",
            description = "Este metodo se encarga de la creacion de un nuevo hotel. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid hotel data or error creating hotel."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/hotels/new")
    public ResponseEntity < ? > addHotel(@RequestBody HotelDTO hotelDTO) {
        List < String > validationErrors = validateHotelDto(hotelDTO);
        try {
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest().body(validationErrors);
            }
            Hotel hotel = hotelService.createHotel(hotelDTO);
            if (hotel == null) {
                return ResponseEntity.badRequest().body("Error creating the hotel, try again.");
            }
            return ResponseEntity.ok().body("Successfully created the hotel");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // BORRAR UN HOTEL POR SU ID
    @Operation(summary = "Borrar un hotel por su ID",
            description = "Este metodo se encarga del borrado de un hotel. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/hotels/{id}")
    public ResponseEntity < ? > deleteHotelById(@PathVariable String id) {
        try {
            Long deletehotelbyId = Long.valueOf(id);
            hotelService.deleteHotelById(deletehotelbyId);
            return ResponseEntity.ok().body("Hotel deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // BUSCAR HOTEL POR FECHA Y DISPONIBILIDAD
    @Operation(summary = "Obtener hoteles por su localizacion y fecha de disponibilidad",
            description = "Este metodo se encarga de obtener todos los hoteles por su localizacion y disponibilidad. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hotels."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotels found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })

    @GetMapping("/hotels/search")
    public ResponseEntity < ? > getHotelsByPlaceAndDate(@RequestParam LocalDate avaliableDateFrom,
                                                        @RequestParam LocalDate avaliableDateTo,
                                                        @RequestParam String location,
                                                        @RequestParam boolean isBooked) {
        try {
            List < Hotel > hotels = hotelService.getHotelsByLocationAndDate(avaliableDateFrom, avaliableDateTo, location, isBooked);
            return ResponseEntity.ok().body(hotels);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // OBTENER UNA LISTA DE HOTELES
    @Operation(summary = "Obtener todos los hoteles",
            description = "Este metodo devuelve todos los hoteles existentes en la base de datos. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all hotels."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotels found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/hotels")
    public ResponseEntity < ? > getAllHotels() {
        try {
            List < Hotel > hotels = hotelService.getAllHotels();
            return ResponseEntity.ok().body(hotels);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // EDITAR UNA HOTEL POR SU ID
    @Operation(summary = "Editar un hotel por su ID",
            description = "Este metodo se encarga de editar un hotel mediante su ID. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel edited successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/hotels/edit/{id}")
    public ResponseEntity < ? > editHotelById(@PathVariable String id, @RequestBody UpdHotelDTO updHotelDTO) {
        try {
            if (!id.matches("^\\d+$")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }
            Long hotelId = Long.valueOf(id);

            // Resto del codigo para poder editar el hotel
            hotelService.editHotelById(hotelId, updHotelDTO);
            return ResponseEntity.ok().body("Hotel edited");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // BUSQUEDA DE HOTEL POR SU ID
    @Operation(summary = "Obtener un hotel por su ID",
            description = "Este metodo se encarga de obtener un hotel mediante su ID. Devuelve diferentes codigos de respuesta HTTP dependiendo de la operacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel information."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel found."),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/hotels/{id}")
    public ResponseEntity < ? > getHotelById(@PathVariable String id) {
        try {
            // Convertir el ID a tipo Long
            Long hotelId = Long.valueOf(id);

            // Obtener el hotel por ID
            Hotel hotel = hotelService.getHotelById(hotelId);

            return ResponseEntity.ok().body(hotel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Variable para almacenar un cod error
    private static final String ERROR_CODE = "Please enter a valid hotel ID || Use only numbers please.";

    // Validaciones usadas a la hora de crear un hotel en el formato para la bbdd
    private void validateField(List < String > errors, String fieldName, String fieldValue, String regex) {
        if (fieldValue == null || fieldValue.isEmpty() || !fieldValue.matches(regex)) {
            errors.add(fieldName + " can only contains letters, try again.");
        }
    }

    private List < String > validateHotelDto(HotelDTO hotelDTO) {
        List < String > validationErrors = new ArrayList < > ();

        // Regex validaciones campos
        validateField(validationErrors, "Hotel name", hotelDTO.getName(), "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ0-9 ]+");
        validateField(validationErrors, "Room type", hotelDTO.getRoomType(), "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+");
        validateField(validationErrors, "Hotel place", hotelDTO.getLocation(), "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+");

        LocalDate currentDate = LocalDate.now();
        if (hotelDTO.getAvaliableDateTo().isBefore(hotelDTO.getAvaliableDateFrom()) ||
                hotelDTO.getAvaliableDateFrom().isBefore(currentDate) ||
                hotelDTO.getAvaliableDateTo().isBefore(currentDate)) {
            validationErrors.add("Check that the dates are correct");
        }
        // Validaciones de campos de precio
        if (hotelDTO.getRoomPrice() <= 0) {
            validationErrors.add("Room price must be greater than 0");
        }
        return validationErrors;
    }
}