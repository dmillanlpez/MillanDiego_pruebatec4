package com.hackaboss.AgenciaTurismo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class HotelReservation {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codHotel;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private int nights;
    private int peopleQ;
    private Double price;
    private String roomType;


    // Relacionamientos
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = @JoinColumn(name = "hotelReservation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "hotelId")
    private Hotel hotel;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "last_deleted_date")
    private LocalDate updated;


}


