package com.hackaboss.AgenciaTurismo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDTO {

    // Atributos
    private String name;
    private String arrival;
    private String departure;
    private LocalDate date;
}
