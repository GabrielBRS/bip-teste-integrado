package com.example.backend;

import java.math.BigDecimal;

public record TransferRequestDTO(
        Long fromId,
        Long toId,
        BigDecimal amount
) {}

