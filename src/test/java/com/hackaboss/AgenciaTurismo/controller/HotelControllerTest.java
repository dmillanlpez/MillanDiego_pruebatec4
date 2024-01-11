package com.hackaboss.AgenciaTurismo.controller;

import com.hackaboss.AgenciaTurismo.DTO.HotelDTO;
import com.hackaboss.AgenciaTurismo.model.Hotel;
import com.hackaboss.AgenciaTurismo.model.Room;
import com.hackaboss.AgenciaTurismo.service.IHotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
public class HotelControllerTest {

    @Autowired
    private HotelController hotelController;

    @Autowired
    private IHotelService hotelService;

    @Test
    public void addHotel_validHotelDto_returns200() {
        // Arrange
        HotelDTO hotelDTO = new HotelDTO("Hotel Alda", "Sada", "individual", 100.0,
                LocalDate.now(), LocalDate.now().plusDays(7), Collections.singletonList(new Room()));
        // Act
        ResponseEntity<?> response = hotelController.addHotel(hotelDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void addHotel_invalidHotelDto_returns400() {
        // Arrange
        HotelDTO hotelDTO = new HotelDTO("TestInv", "01010011 01100001 01100100 01100001", "0", -1.0,
                LocalDate.now(), LocalDate.now().minusDays(1), Collections.singletonList(new Room()));
        // Act
        ResponseEntity<?> response = hotelController.addHotel(hotelDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
