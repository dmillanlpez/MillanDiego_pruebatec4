package com.hackaboss.AgenciaTurismo.DTO;

import com.hackaboss.AgenciaTurismo.model.Room;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class HotelDTO {

    // Atributos
    String name;
    String location;
    Double roomPrice;
    String roomType;
    private LocalDate avaliableDateFrom;
    private LocalDate avaliableDateTo;
    private List<Room> room;

    // Creacion constructor (por eso no se encuentra AllArgsConstructor)

    public HotelDTO(String name, String location, String roomType, Double roomPrice, LocalDate availableDateFrom, LocalDate availableDateTo, List<Room> rooms) {
        this.name = name;
        this.location = location;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.avaliableDateFrom = availableDateFrom;
        this.avaliableDateTo = availableDateTo;
        this.room = rooms;
    }
}
