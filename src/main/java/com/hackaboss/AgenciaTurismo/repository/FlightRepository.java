package com.hackaboss.AgenciaTurismo.repository;

import com.hackaboss.AgenciaTurismo.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {


    @Query("SELECT DISTINCT fli" +
            " from  Flight fli" +
            " where fli.date between :date1 and  :date2 and fli.arrival = :arrival and fli.departure = :departure")
    List<Flight> getAllFlightDate(LocalDate date1, LocalDate date2, String arrival, String departure);

    // Busqueda de un vuelo por su codigo.
    Flight findByCodFlight(String codFlight);
}
