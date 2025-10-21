package com.example.backend;


import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {

    @GetMapping
    public List<String> list() {
        return Arrays.asList("Beneficio A", "Beneficio B");
    }
}
