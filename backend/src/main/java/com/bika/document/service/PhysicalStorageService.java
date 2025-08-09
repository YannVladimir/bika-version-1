package com.bika.document.service;

import com.bika.document.dto.PhysicalStorageLookupDTO;
import com.bika.document.entity.PhysicalStorageColor;
import com.bika.document.entity.PhysicalStorageRoom;
import com.bika.document.repository.PhysicalStorageColorRepository;
import com.bika.document.repository.PhysicalStorageRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhysicalStorageService {
    
    private final PhysicalStorageRoomRepository roomRepository;
    private final PhysicalStorageColorRepository colorRepository;
    
    public PhysicalStorageLookupDTO getPhysicalStorageLookup() {
        log.debug("PhysicalStorageService: Getting physical storage lookup data");
        
        try {
            List<PhysicalStorageRoom> rooms = roomRepository.findAllActiveRooms();
            List<PhysicalStorageColor> colors = colorRepository.findAllActiveColors();
            
            List<String> roomNames = rooms.stream()
                    .map(PhysicalStorageRoom::getName)
                    .collect(Collectors.toList());
            
            List<String> colorNames = colors.stream()
                    .map(PhysicalStorageColor::getName)
                    .collect(Collectors.toList());
            
            // If no data in database, return defaults
            if (roomNames.isEmpty() || colorNames.isEmpty()) {
                log.warn("PhysicalStorageService: No lookup data found in database, returning defaults");
                return PhysicalStorageLookupDTO.getDefaults();
            }
            
            return PhysicalStorageLookupDTO.builder()
                    .rooms(roomNames)
                    .fileColors(colorNames)
                    .sectionColors(colorNames) // Same colors for both file and section
                    .build();
                    
        } catch (Exception e) {
            log.error("PhysicalStorageService: Error getting lookup data, returning defaults", e);
            return PhysicalStorageLookupDTO.getDefaults();
        }
    }
    
    public List<PhysicalStorageRoom> getAllActiveRooms() {
        return roomRepository.findAllActiveRooms();
    }
    
    public List<PhysicalStorageColor> getAllActiveColors() {
        return colorRepository.findAllActiveColors();
    }
    
    public PhysicalStorageRoom createRoom(String name, String description) {
        PhysicalStorageRoom room = PhysicalStorageRoom.builder()
                .name(name)
                .description(description)
                .isActive(true)
                .createdBy("system")
                .updatedBy("system")
                .build();
        return roomRepository.save(room);
    }
    
    public PhysicalStorageColor createColor(String name, String hexValue) {
        PhysicalStorageColor color = PhysicalStorageColor.builder()
                .name(name)
                .hexValue(hexValue)
                .isActive(true)
                .createdBy("system")
                .updatedBy("system")
                .build();
        return colorRepository.save(color);
    }
} 