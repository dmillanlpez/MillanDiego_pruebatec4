package com.hackaboss.AgenciaTurismo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdHotelReservationDTO {

    // Atributos
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
