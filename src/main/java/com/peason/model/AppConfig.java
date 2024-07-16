package com.peason.model;

import com.peason.services.DAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;


@Component
@Configuration()
@PropertySource("classpath:application-${spring.profiles.active}.yml")
public class AppConfig {

    @Autowired
    Environment environment;

    public static String SPRING_ACTIVE_PROFILE = "spring.profiles.active";

    private final String URL = "jdbc-url";
    private final String USER = "username";
    private final String DRIVER = "driver-class-name";
    private final String PASSWORD = "password";
    private final String ALLOWED_TABLES = "allowedtables";
    private final String FRIENDLY_NAME = "friendlyName";
    private final String SCHEMA_NAME = "schema";
    private final String ENABLED = "enabled";

    @Bean
    @ConfigurationProperties(prefix="ct")
    @Qualifier(DAO.DBNAME)
    public DBServer CRYPTO_DBSERVER() {

        String prefix="ct.";
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(environment.getProperty(prefix.concat(URL)));
        driverManagerDataSource.setUsername(environment.getProperty(prefix.concat(USER)));
        driverManagerDataSource.setPassword(environment.getProperty(prefix.concat(PASSWORD)));
        driverManagerDataSource.setDriverClassName(environment.getProperty(prefix.concat(DRIVER)));
        driverManagerDataSource.setSchema(environment.getProperty(prefix.concat(SCHEMA_NAME)));
        DBServer DBServer = new DBServer(driverManagerDataSource);
        DBServer.setFriendlyName(environment.getProperty(prefix.concat(FRIENDLY_NAME)));
        DBServer.setShortName(DAO.DBNAME);
        DBServer.setSchema(environment.getProperty(prefix.concat(SCHEMA_NAME)));
        DBServer.setAvailableTables(new ArrayList<String>(Arrays.asList(environment.getProperty(prefix.concat(ALLOWED_TABLES)).split(","))));
        DBServer.setJdbcTemplate(new JdbcTemplate(driverManagerDataSource));
        DBServer.setEnabled(Boolean.valueOf(environment.getProperty(prefix.concat(ENABLED))));
        return DBServer;
    }

}
