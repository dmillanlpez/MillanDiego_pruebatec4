package com.hackaboss.AgenciaTurismo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonIgnoreProperties({"hotelReservations"})
public class Hotel {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cod_hotel")
    private String codHotel;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "hotel_location")
    private String location;

    @Column(name = "isBooked")
    private boolean isBooked; // la palabra booked genera conflicto si esta sola? revisar futuro

    @JsonIgnore
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "last_deleted_date")
    private LocalDate lastUpdate;

    // Relacionamientos
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<HotelReservation> hotelReservations = new ArrayList<>();

    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL)
    private Room room;

}
