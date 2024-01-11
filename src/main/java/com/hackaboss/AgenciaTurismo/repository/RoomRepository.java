package com.hackaboss.AgenciaTurismo.repository;

import com.hackaboss.AgenciaTurismo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByRoomType(String roomType);
}
