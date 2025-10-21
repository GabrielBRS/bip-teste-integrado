package com.example.backend.integration.ejb;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ejb.beneficio")
public class BeneficioEjbProperties {

    private boolean enabled = false;

    private String jndiName = "java:global/beneficio/BeneficioEjbService";

    private Map<String, String> jndi = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public Map<String, String> getJndi() {
        return jndi;
    }

    public void setJndi(Map<String, String> jndi) {
        this.jndi = jndi;
    }

}
