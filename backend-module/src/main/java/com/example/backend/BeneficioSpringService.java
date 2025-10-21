package com.example.backend;

import com.example.ejb.Beneficio;
import com.example.ejb.BeneficioEjbService;
import jakarta.ejb.EJB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficioSpringService {


    private final BeneficioRepository beneficioRepository;

    @EJB(lookup = "java:global/ejb-module/BeneficioEjbService!com.example.ejb.BeneficioEjbService")
    private BeneficioEjbService beneficioEjbService;

    @Autowired
    public BeneficioSpringService(BeneficioRepository beneficioRepository) {
        this.beneficioRepository = beneficioRepository;
    }

    @Transactional(readOnly = true)
    public List<BeneficioDTO> listarTodos() {
        return beneficioRepository.findAll()
                .stream()
                .map(BeneficioDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BeneficioDTO buscarPorId(Long id) {
        return beneficioRepository.findById(id)
                .map(BeneficioDTO::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benefício não encontrado"));
    }

    @Transactional
    public BeneficioDTO criar(BeneficioDTO dto) {
        Beneficio beneficio = dto.toEntity();
        beneficio.setId(null);
        beneficio = beneficioRepository.save(beneficio);
        return new BeneficioDTO(beneficio);
    }

    @Transactional
    public BeneficioDTO atualizar(Long id, BeneficioDTO dto) {
        Beneficio beneficioExistente = beneficioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benefício não encontrado"));

        Beneficio beneficioAtualizado = dto.toEntity();
        beneficioAtualizado.setId(id);
        beneficioAtualizado.setVersion(beneficioExistente.getVersion());

        beneficioAtualizado = beneficioRepository.save(beneficioAtualizado);
        return new BeneficioDTO(beneficioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!beneficioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Benefício não encontrado");
        }
        beneficioRepository.deleteById(id);
    }

    @Transactional
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
