package com.bika.user.repository;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.user.entity.User;
import com.bika.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u JOIN FETCH u.company WHERE u.username = :username")
    Optional<User> findByUsernameWithCompany(@Param("username") String username);
    
    List<User> findByCompany(Company company);
    List<User> findByDepartment(Department department);
    List<User> findByCompanyAndRole(Company company, UserRole role);
    boolean existsByEmail(String email);
} 