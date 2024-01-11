package com.hackaboss.AgenciaTurismo.repository;

import com.hackaboss.AgenciaTurismo.model.FlightReservation;
import com.hackaboss.AgenciaTurismo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public interface FlightReservationRepository extends JpaRepository<FlightReservation, Long> {

    boolean existsByCodFlight(String flightNumber);

    @Query("SELECT fr FROM FlightReservation fr " +
            "INNER JOIN fr.passengers p " +
            "WHERE p IN :users " +
            "AND fr.lastUpdate IS NULL " +
            "AND fr.date = :date")
    List<FlightReservation> getUserActiveBookings(@Param("users") List<User> users,
                                                  @Param("date") LocalDate date);
}
