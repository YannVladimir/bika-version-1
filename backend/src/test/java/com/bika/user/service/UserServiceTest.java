package com.bika.user.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.common.exception.ResourceNotFoundException;
import com.bika.user.dto.UserDTO;
import com.bika.user.entity.User;
import com.bika.user.entity.UserRole;
import com.bika.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testAdmin;
    private Company testCompany;
    private Department testDepartment;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .id(1L)
                .name("Test Company")
                .code("TEST")
                .active(true)
                .build();

        testDepartment = Department.builder()
                .id(1L)
                .name("Test Department")
                .code("DEPT")
                .company(testCompany)
                .active(true)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .company(testCompany)
                .department(testDepartment)
                .active(true)
                .lastLogin(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAdmin = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@example.com")
                .password("password")
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .company(testCompany)
                .department(testDepartment)
                .active(true)
                .lastLogin(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUserDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .companyId(1L)
                .departmentId(1L)
                .active(true)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser, testAdmin);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserDTO> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        assertEquals(testAdmin.getUsername(), result.get(1).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void getUsersByCompany_ShouldReturnUsersForCompany() {
        // Given
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(userRepository.findByCompany(testCompany)).thenReturn(Arrays.asList(testUser, testAdmin));

        // When
        List<UserDTO> result = userService.getUsersByCompany(1L);

        // Then
        assertEquals(2, result.size());
        assertEquals(testCompany.getId(), result.get(0).getCompanyId());
        assertEquals(testCompany.getId(), result.get(1).getCompanyId());
        verify(companyRepository).findById(1L);
        verify(userRepository).findByCompany(testCompany);
    }

    @Test
    void getUsersByCompany_ShouldThrowException_WhenCompanyNotFound() {
        // Given
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUsersByCompany(999L));
        verify(companyRepository).findById(999L);
    }

    @Test
    void getUsersByDepartment_ShouldReturnUsersForDepartment() {
        // Given
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(userRepository.findByDepartment(testDepartment)).thenReturn(Arrays.asList(testUser, testAdmin));

        // When
        List<UserDTO> result = userService.getUsersByDepartment(1L);

        // Then
        assertEquals(2, result.size());
        assertEquals(testDepartment.getId(), result.get(0).getDepartmentId());
        assertEquals(testDepartment.getId(), result.get(1).getDepartmentId());
        verify(departmentRepository).findById(1L);
        verify(userRepository).findByDepartment(testDepartment);
    }

    @Test
    void getUsersByDepartment_ShouldThrowException_WhenDepartmentNotFound() {
        // Given
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUsersByDepartment(999L));
        verify(departmentRepository).findById(999L);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void getCurrentUserProfile_ShouldReturnCurrentUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDTO result = userService.getCurrentUserProfile();

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getCurrentUserProfile_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUserProfile());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        // Given
        UserDTO updateRequest = UserDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .role(UserRole.MANAGER)
                .active(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.updateUser(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateCurrentUserProfile_ShouldUpdateProfile() {
        // Given
        UserDTO updateRequest = UserDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.updateCurrentUserProfile(updateRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deactivateUser_ShouldDeactivateUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.deactivateUser(1L);

        // Then
        assertNotNull(result);
        assertFalse(result.isActive());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void activateUser_ShouldActivateUser() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.activateUser(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isActive());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }
} 