package com.example.backend.dto;

import java.math.BigDecimal;

public record TransferRequestDTO(
        Long fromId,
        Long toId,
        BigDecimal amount
) {}

