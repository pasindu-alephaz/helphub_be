package lk.helphub.api.infrastructure.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Automatically creates the PostgreSQL database if it does not exist.
 * Implements BeanFactoryPostProcessor to guarantee it runs BEFORE Flyway and datasource initialization.
 */
@Configuration
public class DatabaseInitializer implements BeanFactoryPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        String url = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");

        if (url == null || username == null || password == null) {
            return;
        }

        // Extract database name from JDBC URL (e.g., jdbc:postgresql://localhost:5432/helphub)
        String databaseName = url.substring(url.lastIndexOf("/") + 1);

        // Build URL pointing to the default 'postgres' database
        String defaultUrl = url.substring(0, url.lastIndexOf("/") + 1) + "postgres";

        try (Connection connection = DriverManager.getConnection(defaultUrl, username, password);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'"
            );

            if (!resultSet.next()) {
                statement.executeUpdate("CREATE DATABASE " + databaseName);
                System.out.println("Database '" + databaseName + "' created successfully.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create database '" + databaseName + "': " + e.getMessage(), e);
        }
    }
}
