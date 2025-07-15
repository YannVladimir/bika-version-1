package com.bika.drive.repository;

import com.bika.company.entity.Company;
import com.bika.drive.entity.DriveFolder;
import com.bika.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFolderRepository extends JpaRepository<DriveFolder, Long> {
    
    List<DriveFolder> findByUserAndIsActiveTrue(User user);
    List<DriveFolder> findByUserAndParentIsNullAndIsActiveTrue(User user);
    List<DriveFolder> findByParentAndIsActiveTrue(DriveFolder parent);
    List<DriveFolder> findByCompanyAndIsActiveTrue(Company company);
    
    Optional<DriveFolder> findByUserAndPathAndIsActiveTrue(User user, String path);
    Optional<DriveFolder> findByIdAndUserAndIsActiveTrue(Long id, User user);
    
    @Query("SELECT df FROM DriveFolder df WHERE df.user = :user AND df.parent = :parent AND df.name = :name AND df.isActive = true")
    Optional<DriveFolder> findByUserAndParentAndNameAndIsActiveTrue(@Param("user") User user, @Param("parent") DriveFolder parent, @Param("name") String name);
    
    @Query("SELECT df FROM DriveFolder df WHERE df.user = :user AND df.parent IS NULL AND df.name = :name AND df.isActive = true")
    Optional<DriveFolder> findByUserAndParentIsNullAndNameAndIsActiveTrue(@Param("user") User user, @Param("name") String name);
    
    boolean existsByUserAndParentAndNameAndIsActiveTrue(User user, DriveFolder parent, String name);
    boolean existsByUserAndParentIsNullAndNameAndIsActiveTrue(User user, String name);
} 