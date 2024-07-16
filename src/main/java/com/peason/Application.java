package com.peason;

import com.peason.services.ServersAndTablesRepository;
import com.peason.krakenhandler.FrontEnd;
import com.peason.model.AppConfig;
import com.peason.services.DAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.Arrays;

@Component
@SpringBootApplication
      (exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ComponentScan(basePackages = {"com.peason.*"})
@PropertySource({"application.yml"})
public class Application extends JFrame {


    public static void main(String[] args) {
        String envProfile = System.getenv("CT_ENV") != null ? System.getenv("CT_ENV") :
                "e1";
        System.setProperty(AppConfig.SPRING_ACTIVE_PROFILE, envProfile);
        System.out.println("Environment:" + envProfile);
        //        ApplicationContext ctx = SpringApplication.run(FrontEnd.class, args);
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        FrontEnd frontEnd = ctx.getBean(FrontEnd.class);
        frontEnd.updateUI();

    }
}

