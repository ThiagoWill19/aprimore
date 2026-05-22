# 🚀 Aprimore

Sistema de gestão de ordens de serviço com foco em **facarias e clicherias**, integrando controle operacional e **Planejamento e Controle da Produção (PCP)**.

O objetivo do Aprimore é padronizar processos, reduzir erros operacionais e centralizar informações técnicas em um único sistema.

---

## 🎯 Problema que resolve

Empresas do setor frequentemente enfrentam:

* Informações descentralizadas
* Ordens de serviço inconsistentes
* Falta de controle de prioridade na produção
* Retrabalho por erros de comunicação

O Aprimore resolve isso através de:

✔ Padronização de ordens de serviço
✔ Controle de produção (PCP)
✔ Regras de negócio que evitam erros
✔ Visibilidade operacional via dashboard

---

## ⚙️ Funcionalidades

### 🧾 Gestão de Ordens de Serviço

* Criação de ordens de serviço com validações de domínio
* Associação com cliente, máquina e lâminas
* Validação de consistência de datas
* Normalização de dados técnicos (ex: tipo de onda)
* Geração automática de observações
* Controle de status (OPEN / CLOSED)

---

### 🏭 Planejamento e Controle da Produção (PCP)

* Entrada automática de ordens abertas na fila de produção
* Geração de sequência (`pcpSequence`)
* Ordenação das ordens por prioridade
* Reordenação manual da fila (drag-and-drop)
* Reorganização automática das demais ordens
* Reentrada automática no PCP ao reabrir uma OS

---

### 📊 Dashboard Operacional

* Total de ordens abertas e finalizadas
* Identificação de ordens atrasadas
* Visualização da fila de produção (PCP)
* Destaque de prioridades
* Alertas operacionais

---

### 🔐 Segurança e Multi-tenant

* Isolamento de dados por empresa
* Validação de acesso em todas as operações
* Bloqueio de ações para empresas inativas

---

## 🧠 Regras de Negócio Implementadas

* Não permite OS para máquinas inativas
* Máquina deve pertencer ao cliente informado
* Validação de datas (entrada vs entrega)
* Controle de sequência única no PCP por empresa
* Proteção contra manipulação indevida de prioridades

---

## 🏗️ Arquitetura

* Arquitetura monolítica (MVC)
* Separação de responsabilidades:

  * Controller
  * Service (regras de negócio)
  * Repository
* Uso de DTOs para transporte de dados
* Validação centralizada no domínio

---

## 🛠️ Tecnologias

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL
- ModelMapper (mapeamento entre DTOs e entidades)
- OpenHTMLtoPDF (geração de PDFs)
- Spring Mail (envio de e-mails)
- Lombok

---

# ⚙️ Setup e Execução

## 📋 Pré-requisitos

* Java 17+
* Maven
* MySQL

---

## 🔧 Configuração do ambiente

Todas as credenciais e configurações sensíveis são fornecidas via variáveis de ambiente. Configure-as antes de executar a aplicação.

| Variável | Descrição |
|---|---|
| `DB_NAME` | Nome do banco de dados MySQL |
| `DB_USER` | Usuário do banco de dados |
| `DB_PASS` | Senha do banco de dados |
| `SMTP_HOST` | Host do servidor de e-mail |
| `SMTP_PORT` | Porta do servidor de e-mail |
| `EMAIL` | E-mail usado para envio |
| `SMTP_PASS` | Senha do e-mail |
| `MAIN_NAME` | Nome do administrador principal |
| `MAIN_EMAIL` | E-mail do administrador principal |
| `MAIN_PASS` | Senha do administrador principal |

---

### ✔ application.properties

```properties
spring.application.name=aprimore

spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql: true

spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${EMAIL}
spring.mail.password=${SMTP_PASS}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

main.name=${MAIN_NAME}
main.email=${MAIN_EMAIL}
main.pass=${MAIN_PASS}
```

---

## 🧪 Banco de dados

1. Crie o banco:

```sql
CREATE DATABASE aprimore;
```

2. O Hibernate criará as tabelas automaticamente (se habilitado)

---

## ▶️ Executando a aplicação

A aplicação estará disponível em:

```
http://localhost:8080/login
```

### Para testar a aplicação

**Login admin —** As credenciais do administrador principal são definidas pelas variáveis de ambiente `MAIN_EMAIL` e `MAIN_PASS` configuradas antes da execução.

> O usuário admin é criado automaticamente na primeira execução caso ainda não exista no banco.

- 1 - Acesse a conta admin para cadastrar empresas e usuários e acompanhar os cadastros.
- 2 - Assim que um usuário for cadastrado será enviado o acesso do mesmo por email (utilize um email real para o cadastro)
- 3 - faça login como usuário para ter acesso ao sistema como usuário da plataforma.

---

## 📌 Status do Projeto

🚧 Em desenvolvimento (MVP funcional)

Foco atual:

* Evolução do PCP
* Melhorias no dashboard
* Experiência do usuário
* Validação com usuários reais

---

## 💡 Diferenciais

* Modelagem baseada em problemas reais do setor
* Implementação de fila de produção (PCP)
* Reordenação dinâmica de prioridade
* Regras de domínio para evitar erros operacionais
* Base preparada para evolução para SaaS

---

## 🎯 Próximos Passos

* Relatórios operacionais
* Deploy em ambiente produtivo
* Validação com clientes reais

---

## 🤝 Objetivo do Projeto

Além de resolver um problema real, o Aprimore também é:

* Um projeto de portfólio profissional
* Um experimento de produto SaaS
* Uma vitrine de boas práticas com Java + Spring

---

## ⭐ Contribuição

Este projeto ainda está em evolução, mas sugestões, ideias e feedbacks são muito bem-vindos.
