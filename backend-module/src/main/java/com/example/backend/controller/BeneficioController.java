// Define o pacote ao qual esta classe pertence.
// Pacotes são usados em Java para organizar classes e evitar conflitos de nomes.
package com.example.backend.controller;

// Importa as classes necessárias.
// DTOs (Data Transfer Objects) são usados para transferir dados entre a API e o cliente.
import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferRequest;
// A entidade JPA que representa a tabela do banco de dados.
import com.example.backend.entity.Beneficio;
// A classe de serviço que contém a lógica de negócios.
import com.example.backend.service.BeneficioSpringService;
// Importa a anotação @Valid para habilitar a validação de DTOs de entrada.
import jakarta.validation.Valid;
// Importa as classes de logging (SLF4J) para registrar eventos da aplicação.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Importa anotações do Spring Framework para configuração da API REST.
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @RestController é uma anotação de conveniência do Spring.
 * Ela combina @Controller e @ResponseBody.
 * @Controller: Marca a classe como um "Controller" no padrão MVC (Model-View-Controller).
 * @ResponseBody: Indica que o valor de retorno dos métodos deve ser serializado
 * (geralmente para JSON) e escrito diretamente no corpo da resposta HTTP.
 * Esta classe é responsável por expor os endpoints da API REST para o recurso "Beneficio".
 */
@RestController
/**
 * @RequestMapping define o prefixo da URL base para todos os endpoints neste controller.
 * Todas as rotas definidas aqui começarão com "/api/v1/beneficios".
 */
@RequestMapping("/api/v1/beneficios")
/**
 * @CrossOrigin habilita o CORS (Cross-Origin Resource Sharing) para este controller.
 * Isso permite que aplicações web rodando em outras origens (neste caso, "http://localhost:4200",
 * que é a porta padrão do Angular) possam fazer requisições para esta API.
 * Sem isso, o navegador bloquearia as requisições por padrão (Same-Origin Policy).
 */
@CrossOrigin(origins = "http://localhost:4200")
public class BeneficioController {

    // Declaração da dependência do serviço.
    // A camada de Controller *delega* a lógica de negócios para a camada de Serviço.
    // É 'final' para garantir que seja inicializada no construtor e não possa ser alterada (imutabilidade).
    private final BeneficioSpringService beneficioService;

    // Inicializa um logger estático para esta classe.
    // Usar 'LoggerFactory.getLogger(BeneficioController.class)' é a prática padrão
    // para obter uma instância de logger (via SLF4J) específica para esta classe.
    private static final Logger log = LoggerFactory.getLogger(BeneficioController.class);

    /**
     * Construtor da classe.
     * Este é o método preferido para Injeção de Dependência (DI) no Spring.
     * O Spring automaticamente "injeta" uma instância (Bean) de 'BeneficioSpringService'
     * quando cria uma instância de 'BeneficioController'.
     *
     * @param beneficioService A instância do serviço de benefício gerenciada pelo Spring.
     */
    public BeneficioController(BeneficioSpringService beneficioService) {
        this.beneficioService = beneficioService;
    }

    /**
     * Endpoint para LISTAR todos os benefícios.
     * @GetMapping sem argumentos mapeia requisições HTTP GET para a URL base do controller
     * (ou seja, GET /api/v1/beneficios).
     *
     * @return Uma lista de BeneficioResponse (DTOs) serializada em JSON.
     */
    @GetMapping
    public List<BeneficioResponse> list() {
        // 1. Chama o serviço para buscar todas as *entidades* 'Beneficio' do banco de dados.
        // 2. .stream() converte a lista de entidades em um Java Stream para processamento funcional.
        // 3. .map(BeneficioResponse::from) é um ponto crucial do padrão DTO.
        //    Ele transforma (mapeia) cada entidade 'Beneficio' em um 'BeneficioResponse' (DTO).
        //    'BeneficioResponse::from' é uma referência a um método estático 'from' na classe BeneficioResponse.
        //    Isso evita expor a entidade de domínio 'Beneficio' diretamente na API.
        // 4. .collect(Collectors.toList()) coleta os DTOs processados de volta em uma lista.
        return beneficioService.listAll().stream()
                .map(BeneficioResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Endpoint para BUSCAR um benefício específico pelo seu ID.
     * @GetMapping("/{id}") mapeia requisições HTTP GET para URLs com um ID variável
     * (ex: GET /api/v1/beneficios/123).
     *
     * @param id O ID extraído da URL (path variable).
     * @return O 'BeneficioResponse' (DTO) correspondente ao ID, serializado em JSON.
     */
    @GetMapping("/{id}")
    public BeneficioResponse get(@PathVariable Long id) {
        // @PathVariable "liga" a variável {id} da URL ao parâmetro 'id' do método.
        // 1. Chama o serviço para buscar a entidade pelo ID.
        // 2. Mapeia a entidade 'Beneficio' encontrada para um 'BeneficioResponse' (DTO).
        return BeneficioResponse.from(beneficioService.getById(id));
        // Nota: Se o serviço não encontrar o ID, ele provavelmente lançará uma exceção
        // (ex: ResourceNotFoundException), que deve ser tratada por um @ControllerAdvice
        // para retornar um status 404 Not Found.
    }

    /**
     * Endpoint para CRIAR um novo benefício.
     * @PostMapping mapeia requisições HTTP POST para a URL base
     * (POST /api/v1/beneficios).
     *
     * @ResponseStatus(HttpStatus.CREATED) informa ao Spring para retornar o status HTTP 201 CREATED
     * em vez do padrão (200 OK) quando este método for bem-sucedido.
     *
     * @param request O corpo da requisição JSON, desserializado em um DTO 'BeneficioRequest'.
     * @return O benefício recém-criado, mapeado para um 'BeneficioResponse' (DTO),
     * incluindo o ID gerado pelo banco de dados.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficioResponse create(@Valid @RequestBody BeneficioRequest request) {
        // @RequestBody indica que o corpo da requisição (JSON) deve ser convertido
        // (desserializado) para o objeto 'BeneficioRequest'.
        //
        // @Valid ativa a validação (Jakarta Bean Validation) no DTO 'request'.
        // Se 'BeneficioRequest' tiver anotações como @NotNull, @Size, @Email, etc.,
        // o Spring as validará *antes* de executar o método. Se a validação falhar,
        // o Spring automaticamente retorna um 400 Bad Request.
        //
        // 1. request.toEntity() converte o DTO de *requisição* para a *entidade* 'Beneficio'.
        // 2. beneficioService.create(...) persiste a nova entidade no banco de dados.
        Beneficio created = beneficioService.create(request.toEntity());
        // 3. Mapeia a entidade recém-criada (agora com ID) para um DTO de *resposta*.
        return BeneficioResponse.from(created);
    }

    /**
     * Endpoint para ATUALIZAR um benefício existente.
     * @PutMapping("/{id}") mapeia requisições HTTP PUT para
     * (ex: PUT /api/v1/beneficios/123). PUT é usado para substituição total do recurso.
     *
     * @param id O ID do benefício a ser atualizado (da URL).
     * @param request O corpo da requisição (JSON) com os novos dados (DTO).
     * @return O benefício atualizado, mapeado para 'BeneficioResponse' (DTO).
     */
    @PutMapping("/{id}")
    public BeneficioResponse update(@PathVariable Long id, @Valid @RequestBody BeneficioRequest request) {
        // Combina @PathVariable e @RequestBody: o ID vem da URL, os dados vêm do corpo.
        // @Valid também é usado aqui para garantir que os novos dados sejam válidos.
        // 1. request.toEntity() converte o DTO de entrada para a entidade.
        // 2. beneficioService.update(...) localiza o benefício pelo 'id' e atualiza
        //    seus dados com base na entidade passada.
        Beneficio updated = beneficioService.update(id, request.toEntity());
        // 3. Retorna a entidade atualizada, mapeada para um DTO de resposta.
        return BeneficioResponse.from(updated);
    }

    /**
     * Endpoint para DELETAR um benefício.
     * @DeleteMapping("/{id}") mapeia requisições HTTP DELETE para
     * (ex: DELETE /api/v1/beneficios/123).
     *
     * @ResponseStatus(HttpStatus.NO_CONTENT) informa ao Spring para retornar o status HTTP 204 No Content.
     * Este é o status semanticamente correto para uma operação DELETE bem-sucedida
     * que não retorna nenhum conteúdo no corpo da resposta.
     *
     * @param id O ID do benefício a ser deletado (da URL).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        // O método retorna 'void' porque, após a exclusão, não há o que retornar.
        // O status 204 No Content já informa ao cliente que a operação foi um sucesso.
        beneficioService.delete(id);
    }

    /**
     * Endpoint customizado para realizar uma operação de "transferência".
     * @PostMapping("/transfer") mapeia POST /api/v1/beneficios/transfer.
     *
     * Isso é um exemplo de um endpoint de "ação" ou "comando", que não se encaixa
     * perfeitamente no CRUD padrão (Create, Read, Update, Delete).
     * Ele executa uma lógica de negócios específica (transferir valor).
     *
     * @param req Um DTO 'TransferRequest' contendo os IDs de origem, destino e o valor.
     */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna 204 se a transferência for aceita/processada.
    public void transfer(@RequestBody TransferRequest req) {
        // Loga a requisição de transferência. Essencial para auditoria e debug.
        log.info("Transfer requested: from={} to={} amount={}", req.getFromId(), req.getToId(), req.getAmount());

        // Delega a lógica de negócios complexa (que provavelmente é transacional)
        // para a camada de serviço.
        beneficioService.transfer(req.getFromId(), req.getToId(), req.getAmount());
    }

}