package com.hackaboss.AgenciaTurismo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"flightReservation"})
public class Flight {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cod_flight")
    private String codFlight;

    @Column(name = "flight_departure")
    private String departure;

    @Column(name = "flight_arrival")
    private String arrival;

    @Column(name = "flight_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(name = "last_deleted_date")
    private LocalDate lastUpdate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    // Relacionamientos
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<FlightReservation> flightReservations = new ArrayList<>();

}
