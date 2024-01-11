package com.hackaboss.AgenciaTurismo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdFlightReservationDTO {

    // Atributos
    private String seatType;
    private Double price;
}
