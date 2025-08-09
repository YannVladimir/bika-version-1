package com.bika.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalStorageLookupDTO {
    
    private List<String> rooms;
    private List<String> fileColors;
    private List<String> sectionColors;
    
    // Default values for standardization
    public static PhysicalStorageLookupDTO getDefaults() {
        return PhysicalStorageLookupDTO.builder()
            .rooms(List.of(
                "Archive Room 1",
                "Archive Room 2", 
                "Main Office",
                "Storage Room A",
                "Storage Room B",
                "Basement Archive",
                "Second Floor Archive"
            ))
            .fileColors(List.of(
                "Red",
                "Blue", 
                "Green",
                "Yellow",
                "Black",
                "White",
                "Orange",
                "Purple",
                "Brown",
                "Gray"
            ))
            .sectionColors(List.of(
                "Red",
                "Blue",
                "Green", 
                "Yellow",
                "Black",
                "White",
                "Orange",
                "Purple",
                "Brown",
                "Gray"
            ))
            .build();
    }
} 