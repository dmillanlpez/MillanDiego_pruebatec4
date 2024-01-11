package com.hackaboss.AgenciaTurismo.repository;

import com.hackaboss.AgenciaTurismo.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Hotel findHotelByCodHotel(String codHotel);


    @Query("SELECT  h " +
            "FROM Hotel h " +
            "JOIN h.room r " +
            "WHERE h.location = :location " +
            "  AND h.isBooked = :isBooked " +
            "  AND r.avaliableDateFrom BETWEEN :avaliableDateFrom AND :avaliableDateTo")
    List<Hotel> getHotels(LocalDate avaliableDateFrom, LocalDate avaliableDateTo, String location, boolean isBooked);

    // booked solo generar error?

}
