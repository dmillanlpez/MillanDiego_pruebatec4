package com.hackaboss.AgenciaTurismo.repository;

import com.hackaboss.AgenciaTurismo.model.HotelReservation;
import com.hackaboss.AgenciaTurismo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelReservationRepository extends JpaRepository<HotelReservation, Long> {

    @Query("SELECT hr FROM HotelReservation hr " +
            "INNER JOIN hr.users u " +
            "WHERE (hr.dateFrom BETWEEN :dateFrom AND :dateTo OR hr.dateTo BETWEEN :dateFrom AND :dateTo) " +
            "AND u IN :users")
    List<HotelReservation> getUserAndDateRange(@Param("users") List<User> users,
                                        @Param("dateFrom") LocalDate dateFrom,
                                        @Param("dateTo") LocalDate dateTo);


}
