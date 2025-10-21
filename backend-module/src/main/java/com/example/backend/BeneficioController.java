package com.example.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@CrossOrigin(origins = "http://localhost:4200")
public class BeneficioController {

    private final BeneficioSpringService beneficioService;

    @Autowired
    public BeneficioController(@Lazy BeneficioSpringService beneficioService) {
        this.beneficioService = beneficioService;
    }

    @GetMapping
    public List<BeneficioDTO> list() {
        return beneficioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioDTO> getById(@PathVariable Long id) {
        BeneficioDTO dto = beneficioService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficioDTO create(@RequestBody BeneficioDTO dto) {
        return beneficioService.criar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficioDTO> update(@PathVariable Long id, @RequestBody BeneficioDTO dto) {
        BeneficioDTO atualizado = beneficioService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        beneficioService.deletar(id);
    }

    @PostMapping("/transferir")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@RequestBody TransferRequestDTO request) {
        beneficioService.realizarTransferencia(request);
    }

}

