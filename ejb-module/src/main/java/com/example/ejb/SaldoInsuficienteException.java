package com.example.ejb;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}

