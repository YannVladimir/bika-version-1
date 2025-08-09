package com.bika.document.repository;

import com.bika.document.entity.PhysicalStorageColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicalStorageColorRepository extends JpaRepository<PhysicalStorageColor, Long> {
    
    @Query("SELECT c FROM PhysicalStorageColor c WHERE c.isActive = true ORDER BY c.name")
    List<PhysicalStorageColor> findAllActiveColors();
    
    List<PhysicalStorageColor> findByIsActiveTrueOrderByName();
} 