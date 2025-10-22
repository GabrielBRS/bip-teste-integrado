# Solução Frontend: Angular 20 e Material Design

Este documento detalha a implementação da aplicação frontend para o desafio técnico. A solução foi construída utilizando Angular 20 (com foco em *standalone components*), estilizada com Angular Material, e configurada para consumir o backend Spring Boot.

A seguir, apresentamos a arquitetura, as funcionalidades implementadas e o guia de execução.

## 1. Stack e Arquitetura

* **Stack Tecnológica:** Angular 20 (componentes *standalone*) e Angular Material (tema *prebuilt azure-blue*).
* **Propósito Principal:** Interface para o CRUD de Benefícios e realização de transferências entre contas, consumindo a API backend.
* **Tratamento de Erros:** Um interceptor HTTP global (`HttpErrorInterceptor`) captura erros do backend e exibe mensagens amigáveis ao usuário via `MatSnackBar`.

## 2. Integração com Backend (Endpoints)

A aplicação consome a API RESTful do backend (`/api/v1`) nos seguintes endpoints:

* `GET /beneficios`: Lista todos os benefícios.
* `GET /beneficios/{id}`: Obtém os detalhes de um benefício.
* `POST /beneficios`: Cria um novo benefício.
* `PUT /beneficios/{id}`: Atualiza um benefício existente.
* `DELETE /beneficios/{id}`: Remove um benefício.
* `POST /beneficios/transfer`: Executa a transferência (payload: `{ fromId, toId, amount }`).

**Respostas de Erro:** A API retorna um JSON no formato `{ "error": "mensagem" }` para status `400/422/404`, que é tratado pelo interceptor.

## 3. Estrutura de Arquivos Relevantes

* `frontend/src/app/app.ts`: Componente raiz, *toolbar* e navegação principal.
* `frontend/src/app/app.routes.ts`: Definição das rotas *standalone* (com *lazy loading*).
* `frontend/src/app/core/models/beneficio.model.ts`: Tipos `Beneficio`, `BeneficioRequest` e `TransferRequest`.
* `frontend/src/app/core/service/beneficio-service.ts`: Cliente HTTP centralizado (CRUD + transferência).
* `frontend/src/app/core/interceptors/error.interceptor.ts`: Interceptor global para erros (exibe `MatSnackBar`).
* `frontend/src/app/component/beneficio/beneficio.html`: Tabela com filtro, paginação e ações.
* `frontend/src/app/component/transfer/transfer.html`: Tela de transferência com validações.
* `frontend/src/environments/*`: Arquivos de ambiente (`apiUrl: '/api/v1'`).
* `frontend/angular.json`: Configuração do projeto (build, tema Material, *file replacements*).

## 4. Guia de Execução Local

### Pré-requisitos

* Node.js (v20 LTS).
* A aplicação backend (Spring Boot) deve estar em execução em `http://localhost:8080`.

### Primeira Execução (Passo a Passo)

1.  **Navegue até o diretório** do frontend:
    ```bash
    cd frontend
    ```
2.  **Instale as dependências** do Node.js:
    ```bash
    npm install
    ```
3.  **Inicie o servidor de desenvolvimento** (com proxy):
    ```bash
    npm start
    ```
4.  Acesse a aplicação no seu navegador: `http://localhost:4200`

### Configuração do Proxy

O servidor de desenvolvimento (`npm start`) utiliza um proxy para evitar problemas de CORS.

* O arquivo `frontend/proxy.conf.json` redireciona todas as requisições de `/api` para o backend em `http://localhost:8080`.
* O código da aplicação utiliza `environment.apiUrl = '/api/v1'`, garantindo que o *proxy* seja usado.
* **Ajuste:** Caso o backend esteja rodando em outra porta, altere a propriedade `target` no `proxy.conf.json`.

## 5. Build de Produção

Para gerar os *bundles* de produção:

1.  Execute o script de *build*:
    ```bash
    cd frontend
    npm run build
    ```
2.  Os arquivos otimizados serão gerados em `frontend/dist/tmp`.

## 6. Funcionalidades Implementadas

* **Lista de Benefícios:**
    * Apresenta uma `MatTable` com filtro de texto, ordenação e paginação (processados no *client-side*).
    * Permite as ações: Criar, Editar, Excluir (com diálogo de confirmação) e Iniciar Transferência.
* **Formulário de Benefício (Criar/Editar):**
    * Utiliza *Reactive Forms* para validação.
    * Campos: nome (obrigatório), descrição, valor (obrigatório, ≥ 0) e *status* (ativo/inativo).
* **Tela de Transferência:**
    * Permite selecionar origem e destino (dados carregados do `BeneficiosService`).
    * Validações incluem valor (obrigatório, > 0) e impedem a transferência para a mesma conta.
    * Suporta pré-seleção da origem via *query param* (`?fromId=`).

## 7. Rotas da Aplicação

* `/beneficios`: Lista de benefícios
* `/beneficios/new`: Formulário de criação
* `/beneficios/:id`: Formulário de edição
* `/beneficios/transfer`: Tela de transferência

## 8. Notas Técnicas e Troubleshooting

* **Injeção de Dependências:** O `HttpClient` e o `ErrorInterceptor` são configurados globalmente no `app.config.ts`.
* **Tema:** O tema do Angular Material (`azure-blue`) é importado no `frontend/angular.json` (seção `styles`).
* **Ordem das Rotas:** A rota `beneficios/transfer` é declarada antes de `beneficios/:id` em `app.routes.ts` para evitar conflito de *matching*.
* **Erro de CORS:** Certifique-se de estar executando o projeto com `npm start`, que ativa o *proxy*. Verifique o `frontend/proxy.conf.json`.
* **Backend em outra porta:** Se o backend não estiver na porta `8080`, ajuste a propriedade `target` no `proxy.conf.json`.
* **Aviso de *Budget*:** O Angular pode avisar sobre o tamanho dos *bundles* no *build*. Os limites podem ser ajustados em `frontend/angular.json` (seção `budgets`).