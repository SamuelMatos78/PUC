# Secure Login — PUC Minas

Aplicação acadêmica com Spring Boot 4.1.1, Java 25, Maven, Thymeleaf, Spring Data JPA, Spring Security e H2 persistente.

## Executar

Com JDK 25 instalado, na raiz do projeto:

```sh
./mvnw clean test
./mvnw clean package
java -jar target/secure-login-0.0.1-SNAPSHOT.jar
```

Também é possível iniciar com `./mvnw spring-boot:run`.
Acesse http://localhost:8081/login e crie uma conta em `/register`.

## Funcionamento

- `/register` (GET/POST): campos obrigatórios, email válido, senha confirmada (mínimo de 8 caracteres e máximo de 72 bytes), verificação de duplicidade e BCrypt. Username e email também possuem restrições únicas no banco.
- `/login` (GET/POST): o controller renderiza a página; Spring Security processa o POST, compara o hash BCrypt e gerencia a sessão.
- `/` e `/home`: exigem autenticação.
- `/logout` (POST): invalida a sessão. Use o botão Sair da conta.
- `/recoverpassword` (GET/POST): simulação acadêmica que consulta o email e responde igualmente para emails cadastrados ou desconhecidos. Não envia email nem altera senhas.
- Os formulários usam `th:action`, que integra os tokens CSRF do Spring Security. Não há SQL concatenado.
- A imagem existente `src/main/resources/static/images/puc.jpeg` é utilizada nas quatro páginas.

O banco fica em `data/login.mv.db`, relativo ao diretório de execução. Reinicie a aplicação **a partir da mesma pasta** para manter os cadastros. O console H2 está desabilitado. Os testes automatizados usam H2 em memória e não apagam o banco da aplicação.

Não há criação automática de usuários. As dependências adicionadas são `spring-boot-starter-security` (inclui a biblioteca BCrypt já utilizada) e `spring-security-test` (somente testes).


## Habilitar envio do email

Exporte como variavel de ambiente os itens:
```bash
MAIL_USERNAME
MAIL_PASSWORD
``` 
por padrão vem configurado compativél com o gmail podendo ser alterado em application.properties