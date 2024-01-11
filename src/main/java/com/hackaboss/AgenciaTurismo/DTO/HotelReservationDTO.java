package com.hackaboss.AgenciaTurismo.DTO;

import com.hackaboss.AgenciaTurismo.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReservationDTO {

    // Atributos
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String location;
    private String codHotel;
    private String roomType;
    private List<User> hosts;
}
