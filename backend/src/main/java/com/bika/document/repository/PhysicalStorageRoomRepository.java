package com.bika.document.repository;

import com.bika.document.entity.PhysicalStorageRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicalStorageRoomRepository extends JpaRepository<PhysicalStorageRoom, Long> {
    
    @Query("SELECT r FROM PhysicalStorageRoom r WHERE r.isActive = true ORDER BY r.name")
    List<PhysicalStorageRoom> findAllActiveRooms();
    
    List<PhysicalStorageRoom> findByIsActiveTrueOrderByName();
} 