# API Spring Boot – Oracle

API para consulta de metadados/tabelas em Oracle e cadastro/autenticação de usuários com Spring Security (sessão via cookie).

## Pré-requisitos
- JDK 17 e Maven (mvnw incluso).
- Banco Oracle acessível e credenciais válidas.
- Ajuste `src/main/resources/application.properties` com `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, `app.oracle.default-schema` e, se quiser, `server.port` (padrão 8082).

## Como executar
1. Instale dependências e rode a aplicação: `./mvnw spring-boot:run`
2. A API sobe em `http://localhost:8082` (ou porta configurada).

## Autenticação
- Endpoints públicos: `/api/users/register`, `/api/status/**`, `/api/metadata/**`.
- Os demais exigem login de sessão (cookie `JSESSIONID` gerado em `/login`).
- Para logar, envie `username` (email cadastrado) e `password` em `application/x-www-form-urlencoded` para `POST /login`. Exemplo com curl:
  ```bash
  curl -i -c cookies.txt -d "username=usuario@dominio.com&password=senha123" http://localhost:8082/login
  ```
- Use o cookie salvo para chamadas protegidas; faça `POST /logout` para encerrar.

## Endpoints principais
- `GET /api/status/db-teste`  
  Verifica conectividade com o Oracle.  
  Exemplo: `curl http://localhost:8082/api/status/db-teste`

- `GET /api/metadata/tables?owner=RM554327`  
  Lista tabelas visíveis. Se não informar `owner`, usa `app.oracle.default-schema`.

- `GET /api/metadata/data/{tableName}?owner=RM554327`  
  Retorna todos os registros da tabela informada. Responde `204` se a tabela estiver vazia.

- `POST /api/users/register`  
  Cadastra novo usuário (associa à empresa ID 1).  
  Body `application/json`:
  ```json
  {
    "nome": "Fulano da Silva",
    "email": "fulano@dominio.com",
    "senha": "senhaForte123"
  }
  ```

- `POST /login`  
  Cria sessão. Body `application/x-www-form-urlencoded` com `username` (email) e `password`.

- `POST /logout`  
  Encerra sessão e remove o cookie.

## Coleção Postman
Há uma coleção pronta em `docs/APISpring.postman_collection.json` com:
- Health check do banco;
- Listagem de tabelas;
- Consulta de dados por tabela;
- Cadastro de usuário;
- Login com cookie;
- Logout.

Para usar, importe o JSON no Postman/Insomnia. A variável `baseUrl` já aponta para `http://localhost:8082`; altere se mudar a porta ou host.

## Observações sobre o banco
- O schema padrão vem de `app.oracle.default-schema`. Informe `owner` nas consultas se quiser outro schema.
- A `UserService` assume que existe `EMPRESA` com `ID = 1` para vincular novos usuários. Ajuste se sua estrutura for diferente.
