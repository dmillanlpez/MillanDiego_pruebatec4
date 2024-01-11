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

        boolean anyInactiveHotel = hotels.stream().anyMatch(hotel -> !hotel.isDeleted());

        if (anyInactiveHotel) {
            return hotels.stream()
                    .filter(hotel -> !hotel.isDeleted())
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("There are no inactive hotels in the database.");
        }

    }

    @Override
    public void deleteHotelById(Long id) {
        // Busco el hotel por su ID en la base de datos
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);

        // Verifico si el hotel con el ID proporcionado existe en la base de datos
        if (optionalHotel.isEmpty()) {
            throw new IllegalArgumentException("The hotel is not found in the database.");
        }

        // Obtengo el hotel si se encuentra presente en la base de datos
        Hotel hotel = optionalHotel.get();

        // Verifico si el hotel se encuentra marcado como eliminado en la base de datos
        if (hotel.isDeleted()) {
            throw new IllegalArgumentException("The hotel that you are trying to delete is already deleted or is not found.");
        }

        // Verifico si el hotel se encuentra reservado
        if (hotel.isBooked()) {
            // Verifico si el hotel tiene alguna reserva marcada como eliminada
            if (!hotel.getHotelReservations().isEmpty() && hotel.getHotelReservations().stream().anyMatch(HotelReservation::isDeleted)) {
                // Si tiene una reserva con deleted(t) marcado y se encuentra reservado, se marcara como eliminado
                hotel.setDeleted(true);
                hotel.setLastUpdate(LocalDate.now());
                hotelRepository.save(hotel);
            } else {
                throw new IllegalArgumentException("The hotel has a reservation.");
            }
        } else {
            // En caso de nor estar marcado ocmo reservado, se procede a borrar el hotel
            hotel.setDeleted(true);
            hotel.setLastUpdate(LocalDate.now());
            hotelRepository.save(hotel);
        }
    }

    // Edita un hotel por su ID en la base de datos
    @Override
    public Hotel editHotelById(Long id, UpdHotelDTO hotelDTO) {
        Optional<Hotel> opHotel = hotelRepository.findById(id);
        Hotel hotel = opHotel.orElseThrow(() -> new IllegalArgumentException("The hotel is not found in the database."));

        if (hotel.isDeleted()) {
            throw new IllegalArgumentException("The hotel is not found in the database.");
        }

        if (hotel.isBooked()) {
            throw new IllegalArgumentException("The hotel has a reservation.");
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

        // Verifico si el hotel con el ID proporcionado existe en la base de datos
        if (optionalHotel.isPresent()) {

            // Obtengo el hotel si se encuentra presente en la base de datos
            Hotel hotel = optionalHotel.get();

            // Verifico si el hotel se encuentra marcado como eliminado en la base de datos
            if (hotel.isDeleted()) {
                throw new IllegalArgumentException("The hotel is not found in the database.");
            }
            // Retorno el hotel encontrado por su id
            return hotelRepository.findById(id).orElse(null);
        }
        // En caso de no encontrar el hotel en la base de datos se lanza una excepcion con el mensaje de error
        throw new IllegalArgumentException("The hotel with ID " + id + " is not found in the database.");
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
