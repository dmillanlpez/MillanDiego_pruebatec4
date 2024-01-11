package com.hackaboss.AgenciaTurismo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdFlightDTO {

    // Atributos
    private LocalDate date;
    private String seatType;

}
