package com.epam.gymapp.ops;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT 1");
                ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next() && resultSet.getInt(1) == 1) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connection successful")
                        .build();
            }

            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "SELECT 1 failed")
                    .build();

        } catch (Exception ex) {
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("error", ex.getClass().getSimpleName())
                    .withDetail("message", ex.getMessage())
                    .build();
        }
    }
}