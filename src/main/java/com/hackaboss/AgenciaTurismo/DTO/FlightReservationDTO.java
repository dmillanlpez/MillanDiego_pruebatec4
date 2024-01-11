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
public class FlightReservationDTO {

    // Atributos
    private LocalDate date;
    private String departure;
    private String arrival;
    private String codFlight;
    private String seatType;
    private double price;
    private List<User> passengers;
}
