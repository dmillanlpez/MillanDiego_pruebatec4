package com.hackaboss.AgenciaTurismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String passport;
    private int age;

    // Relacionamentos
    @ToString.Exclude
    @ManyToMany(mappedBy = "passengers", cascade = CascadeType.ALL)
    private List<FlightReservation> flightReservations = new ArrayList<>();


    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<HotelReservation> hotels = new ArrayList<>();

    // Metodo toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", passport='" + passport + '\'' +
                ", age=" + age +
                '}';
    }
}
