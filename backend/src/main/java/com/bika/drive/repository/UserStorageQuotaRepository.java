package com.bika.drive.repository;

import com.bika.company.entity.Company;
import com.bika.drive.entity.UserStorageQuota;
import com.bika.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStorageQuotaRepository extends JpaRepository<UserStorageQuota, Long> {
    
    Optional<UserStorageQuota> findByUserAndCompany(User user, Company company);
    Optional<UserStorageQuota> findByUser(User user);
    List<UserStorageQuota> findByCompany(Company company);
    
    @Modifying
    @Query("UPDATE UserStorageQuota usq SET usq.usedStorageBytes = usq.usedStorageBytes + :sizeChange WHERE usq.user = :user AND usq.company = :company")
    void updateUsedStorage(@Param("user") User user, @Param("company") Company company, @Param("sizeChange") Long sizeChange);
    
    @Query("SELECT usq FROM UserStorageQuota usq WHERE usq.usedStorageBytes > usq.maxStorageBytes")
    List<UserStorageQuota> findUsersExceedingQuota();
    
    @Query("SELECT usq FROM UserStorageQuota usq WHERE (usq.usedStorageBytes * 100.0 / usq.maxStorageBytes) >= :percentage")
    List<UserStorageQuota> findUsersWithUsageAbove(@Param("percentage") double percentage);
} 