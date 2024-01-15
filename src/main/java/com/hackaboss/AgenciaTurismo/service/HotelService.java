package com.hackaboss.AgenciaTurismo.service;

import com.hackaboss.AgenciaTurismo.DTO.HotelDTO;
import com.hackaboss.AgenciaTurismo.DTO.UpdHotelDTO;
import com.hackaboss.AgenciaTurismo.model.Hotel;
import com.hackaboss.AgenciaTurismo.model.HotelReservation;
import com.hackaboss.AgenciaTurismo.model.Room;
import com.hackaboss.AgenciaTurismo.repository.HotelRepository;
import com.hackaboss.AgenciaTurismo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class HotelService implements IHotelService {

    // Inyeccion de dependencia de los repositorios de hotel y habitacion
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;

    // Crea un nuevo hotel a partir de los datos proporcionados en el DTO
    @Override
    public Hotel createHotel(HotelDTO hotelDTO) {

        // Creación de un Hotel
        Hotel hotel = new Hotel();

        // Establece (nombre, localizacion, si esta ocupado o si se ha borrado)
        hotel.setHotelName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setBooked(false);
        hotel.setDeleted(false);

        // Generacion de un codigo aleatorio para el hotel
        String hotelCodeGen = generateHotelCode(hotelDTO.getName());
        hotel.setCodHotel(hotelCodeGen);

        // Creacion de una habitacion de un hotel
        Room room = new Room();

        // Establece (tipo de habitacion, precio, y el tiempo que esta disponible)
        room.setRoomType(hotelDTO.getRoomType());
        room.setRoomPrice(hotelDTO.getRoomPrice());
        room.setAvaliableDateFrom(hotelDTO.getAvaliableDateFrom());
        room.setAvaliableDateTo(hotelDTO.getAvaliableDateTo());

        // Se guarda la habitacion y el hotel en la base de datos
        Hotel savedHotel = hotelRepository.save(hotel);

        room.setHotel(savedHotel);

        roomRepository.save(room);

        return savedHotel;

    }

    // Busca todos los hoteles que cumplen con los criterios especificados
    @Override
    public List<Hotel> getHotelsByLocationAndDate(LocalDate avaliableDateFrom, LocalDate avaliableDateTo, String location, boolean booked) {

        // Realizar la busqueda de hoteles en la base de datos
        List<Hotel> hotels = hotelRepository.getHotels(avaliableDateFrom, avaliableDateTo, location, booked);

        // Validar que se hayan encontrado hoteles que coincidan con los criterios
        if (hotels.isEmpty() || hotels.stream().anyMatch(Hotel::isDeleted)) {
            throw new IllegalArgumentException("There are no hotels with these criteria in the database.");
        }
        // Devolver la lista de hoteles encontrados
        return hotels;
    }

    @Override
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();

        // Verificar si hay algun hotel inactivo en la lista
        boolean anyInactiveHotel = hotels.stream().anyMatch(hotel -> !hotel.isDeleted());

        if (anyInactiveHotel) {
            // Filtrar la lista para incluir solo hoteles no eliminados
            return hotels.stream()
                    .filter(hotel -> !hotel.isDeleted())
                    .collect(Collectors.toList());
        } else {
            // En caso de no haber hoteles no eliminados, lanzar una excepcion
            throw new IllegalArgumentException("There are no active hotels in the database.");
        }
    }


    @Override
    public void deleteHotelById(Long id) {
        // Buscar el hotel por su ID en la base de datos
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);

        // Verificar si el hotel con el ID proporcionado existe en la base de datos
        if (optionalHotel.isEmpty()) {
            throw new IllegalArgumentException("The hotel with ID " + id + " was not found in the database.");
        }

        // Obtener el hotel si se encuentra presente en la base de datos
        Hotel hotel = optionalHotel.get();

        // Verificar si el hotel esta marcado como eliminado en la base de datos
        if (hotel.isDeleted()) {
            throw new IllegalArgumentException("The hotel with ID " + id + " is already deleted or not found.");
        }

        // Verificar si el hotel esta reservado
        if (hotel.isBooked()) {
            // Verificar si el hotel tiene alguna reserva marcada como eliminada
            if (!hotel.getHotelReservations().isEmpty() && hotel.getHotelReservations().stream().anyMatch(HotelReservation::isDeleted)) {
                // Si tiene una reserva con deleted(t) marcado y esta reservado, se marcara como eliminado
                hotel.setDeleted(true);
                hotel.setLastUpdate(LocalDate.now());
                hotelRepository.save(hotel);
            } else {
                throw new IllegalArgumentException("The hotel with ID " + id + " has an active reservation and cannot be deleted.");
            }
        } else {
            // En caso de no estar marcado como reservado, se procede a marcar el hotel como eliminado
            hotel.setDeleted(true);
            hotel.setLastUpdate(LocalDate.now());
            hotelRepository.save(hotel);
        }
    }


    // Edita un hotel por su ID en la base de datos
    @Override
    public Hotel editHotelById(Long id, UpdHotelDTO hotelDTO) {
        Optional<Hotel> opHotel = hotelRepository.findById(id);
        Hotel hotel = opHotel.orElseThrow(() -> new IllegalArgumentException("Hotel not found in the database."));

        if (hotel.isDeleted()) {
            throw new IllegalArgumentException("The hotel is marked as deleted in the database.");
        }

        if (hotel.isBooked()) {
            throw new IllegalArgumentException("The hotel has an active reservation and cannot be edited.");
        }

        hotel.setHotelName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setCodHotel(generateHotelCode(hotelDTO.getName()));
        hotel.setLastUpdate(LocalDate.now());

        return hotelRepository.save(hotel);
    }

    @Override
    public Hotel getHotelById(Long id) {
        // Buscar el hotel por su ID en la base de datos
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);

        // Verificar si el hotel con el ID proporcionado existe en la base de datos
        if (optionalHotel.isPresent()) {

            // Obtener el hotel si se encuentra presente en la base de datos
            Hotel hotel = optionalHotel.get();

            // Verificar si el hotel está marcado como eliminado en la base de datos
            if (hotel.isDeleted()) {
                throw new IllegalArgumentException("The hotel with ID " + id + " has been deleted in the database.");
            }
            // Retornar el hotel encontrado por su ID
            return hotel;
        }
        // En caso de no encontrar el hotel en la base de datos, lanzar una excepción con un mensaje descriptivo
        throw new IllegalArgumentException("The hotel with ID " + id + " does not exist in the database.");
    }

    // Metodos que genera un codigo aleatorio para el hotel
    // Generar Codigo del hotel
    private String generateHotelCode(String hotelName) {
        String[] words = hotelName.split("\\s+|-");

        StringBuilder code = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                code.append(word.substring(0, Math.min(2, word.length())).toUpperCase());
            }
        }
        // Generar una secuencia de numeros aleatorios
        Random random = new Random();
        code.append("-");
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

}
