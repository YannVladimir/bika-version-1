package com.bika.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDatabaseConnection() throws SQLException {
        assertNotNull(dataSource, "DataSource should not be null");
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1), "Connection should be valid");
            System.out.println("Connected to database: " + connection.getCatalog());
        }
    }
} 