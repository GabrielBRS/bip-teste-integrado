package com.example.backend.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.backend")
public class JpaConfig {

    @PersistenceUnit(unitName = "default")
    private EntityManagerFactory emf;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(emf);
    }

}