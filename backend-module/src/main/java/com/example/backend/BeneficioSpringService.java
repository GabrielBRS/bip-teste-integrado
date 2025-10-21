package com.example.backend;

import com.example.ejb.Beneficio;
import com.example.ejb.BeneficioEjbService;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficioSpringService {

//    @EJB(lookup = "java:global/ear-module-0.0.1-SNAPSHOT/ejb-module/BeneficioEjbService!com.example.ejb.BeneficioEjbService")
    private BeneficioEjbService beneficioEjbService;

    public List<BeneficioDTO> listarTodos() {
        return beneficioEjbService.listarTodos()
                .stream()
                .map(BeneficioDTO::new)
                .collect(Collectors.toList());
    }

    public BeneficioDTO buscarPorId(Long id) {
        Beneficio beneficio = beneficioEjbService.buscarPorId(id);
        if (beneficio == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Benefício não encontrado");
        }
        return new BeneficioDTO(beneficio);
    }

    public BeneficioDTO criar(BeneficioDTO dto) {
        Beneficio beneficio = dto.toEntity();
        beneficio = beneficioEjbService.criar(beneficio);
        return new BeneficioDTO(beneficio);
    }

    public BeneficioDTO atualizar(Long id, BeneficioDTO dto) {
        Beneficio beneficioAtualizado = dto.toEntity();
        beneficioAtualizado.setId(id);
        beneficioAtualizado = beneficioEjbService.atualizar(beneficioAtualizado);
        return new BeneficioDTO(beneficioAtualizado);
    }

    public void deletar(Long id) {
        beneficioEjbService.deletar(id);
    }

    public void realizarTransferencia(TransferRequestDTO request) {
        try {
            beneficioEjbService.transfer(
                    request.fromId(),
                    request.toId(),
                    request.amount()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}