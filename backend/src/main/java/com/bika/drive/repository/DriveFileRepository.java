package com.bika.drive.repository;

import com.bika.company.entity.Company;
import com.bika.drive.entity.DriveFile;
import com.bika.drive.entity.DriveFolder;
import com.bika.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFileRepository extends JpaRepository<DriveFile, Long> {
    
    List<DriveFile> findByUserAndIsActiveTrue(User user);
    List<DriveFile> findByUserAndIsDeletedFalseAndIsActiveTrue(User user);
    List<DriveFile> findByFolderAndIsDeletedFalseAndIsActiveTrue(DriveFolder folder);
    List<DriveFile> findByUserAndFolderIsNullAndIsDeletedFalseAndIsActiveTrue(User user);
    List<DriveFile> findByCompanyAndIsActiveTrue(Company company);
    
    Optional<DriveFile> findByIdAndUserAndIsActiveTrue(Long id, User user);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND df.folder = :folder AND df.name = :name AND df.isDeleted = false AND df.isActive = true")
    Optional<DriveFile> findByUserAndFolderAndNameAndIsDeletedFalseAndIsActiveTrue(@Param("user") User user, @Param("folder") DriveFolder folder, @Param("name") String name);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND df.folder IS NULL AND df.name = :name AND df.isDeleted = false AND df.isActive = true")
    Optional<DriveFile> findByUserAndFolderIsNullAndNameAndIsDeletedFalseAndIsActiveTrue(@Param("user") User user, @Param("name") String name);
    
    @Query("SELECT SUM(df.fileSize) FROM DriveFile df WHERE df.user = :user AND df.isDeleted = false AND df.isActive = true")
    Long calculateTotalFileSizeByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(df) FROM DriveFile df WHERE df.user = :user AND df.isDeleted = false AND df.isActive = true")
    Long countFilesByUser(@Param("user") User user);
    
    @Query("SELECT df FROM DriveFile df WHERE df.user = :user AND df.mimeType LIKE :mimeTypePattern AND df.isDeleted = false AND df.isActive = true")
    List<DriveFile> findByUserAndMimeTypeContainingAndIsDeletedFalseAndIsActiveTrue(@Param("user") User user, @Param("mimeTypePattern") String mimeTypePattern);
    
    boolean existsByUserAndFolderAndNameAndIsDeletedFalseAndIsActiveTrue(User user, DriveFolder folder, String name);
    boolean existsByUserAndFolderIsNullAndNameAndIsDeletedFalseAndIsActiveTrue(User user, String name);
} 