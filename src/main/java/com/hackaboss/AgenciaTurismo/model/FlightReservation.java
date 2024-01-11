package com.hackaboss.AgenciaTurismo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class FlightReservation {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String departure;
    private String arrival;
    private String codFlight;
    private Integer peopleQ;
    private String seatType;
    private Double price;

    // Relacionamientos
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "flightReservation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> passengers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_flight")
    private Flight flight;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "last_deleted_date")
    private LocalDate lastUpdate;

    @Override
    public String toString() {
        return "BookFlight{" +
                "id=" + id +
                ", date=" + date +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", codFlight='" + codFlight + '\'' +
                ", peopleQ=" + peopleQ +
                ", seatType='" + seatType + '\'' +
                ", price=" + price +
                ", isDeleted=" + isDeleted +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
