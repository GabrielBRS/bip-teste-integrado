package com.example.backend.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.backend")
public class JpaConfig {

    @Bean
    public JndiObjectFactoryBean entityManagerFactoryLookup() {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName("java:jboss/EntityManagerFactory/ear-module-0.0.1-SNAPSHOT/ejb-module.jar#default");
        factoryBean.setExpectedType(EntityManagerFactory.class);
        return factoryBean;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(JndiObjectFactoryBean emfLookup) throws IllegalArgumentException, NamingException {
        return (EntityManagerFactory) emfLookup.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}