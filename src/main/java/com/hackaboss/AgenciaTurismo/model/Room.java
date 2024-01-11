package com.hackaboss.AgenciaTurismo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Room {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomType;
    private Double roomPrice;
    private LocalDate avaliableDateFrom;
    private LocalDate avaliableDateTo;

    // Relacionamientos
    @OneToOne
    @JoinColumn(name = "hotel_id", unique = true)
    @JsonBackReference
    private Hotel hotel;
}
