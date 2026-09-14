# Proposta da Atividade

## Desenvolvimento de Sistema Web com Spring Boot e Thymeleaf

A proposta desta atividade é desenvolver uma aplicação web utilizando **Java com Spring Boot** no back-end e **Thymeleaf** para renderização das páginas HTML.

O sistema deve implementar funcionalidades básicas de autenticação e gerenciamento de usuários, além de integração com serviço de envio de e-mails.

## Funcionalidades

A aplicação deve possuir:

- Tela de login para autenticação de usuários.
- Tela de cadastro para criação de novos usuários.
- Armazenamento seguro das senhas dos usuários.
- Validação de dados no cadastro.
- Recuperação de senha por meio de solicitação via e-mail.
- Envio de e-mail utilizando integração SMTP.
- Controle de acesso às páginas protegidas.
- Sessão de usuário após autenticação.
- Opção de logout.

## Tecnologias Utilizadas

- Java
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Thymeleaf
- H2 Database
- JavaMailSender
- HTML
- CSS

## Fluxo Principal

O usuário pode criar uma conta informando seus dados e uma senha.

Após o cadastro, pode acessar a tela de login e autenticar-se no sistema.

Caso esqueça a senha, pode utilizar a tela de recuperação, informando seu e-mail. A aplicação verifica a solicitação e utiliza o serviço de e-mail configurado para enviar as instruções de recuperação.

As páginas restritas da aplicação só podem ser acessadas após autenticação.

## Objetivo

O objetivo da atividade é aplicar, de forma prática, os principais conceitos de desenvolvimento de aplicações web com Spring Boot, incluindo:

- criação de rotas;
- processamento de formulários;
- integração entre back-end e front-end com Thymeleaf;
- persistência de dados;
- autenticação e autorização;
- segurança de senhas;
- integração com serviços externos de e-mail.