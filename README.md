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
- PostgreSQL / MySQL
- ModelMapper (mapeamento entre DTOs e entidades)
- OpenHTMLtoPDF (geração de PDFs)
- Spring Mail (envio de e-mails)
- Lombok

---

# ⚙️ Setup e Execução

## 📋 Pré-requisitos

* Java 17+
* Maven ou Gradle
* MySQL

---

## 🔧 Configuração do ambiente

Você pode configurar via variáveis de ambiente ou diretamente no `application.properties`.

### ✔ Variáveis de ambiente

```bash
DB_URL=jdbc:mysql://localhost:5432/aprimore
DB_USERNAME=postgres
DB_PASSWORD=senha
```

---

### ✔ application.properties

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
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

### Com Maven

```bash
./mvnw spring-boot:run
```

### Com Gradle

```bash
./gradlew bootRun
```

---

A aplicação estará disponível em:

```
http://localhost:8080
```

---

## 🔄 Profiles (opcional)

Você pode usar profiles para diferentes ambientes:

```properties
spring.profiles.active=dev
```

Sugestão:

* `dev` → ambiente local
* `prod` → produção

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

* Autenticação multi-empresa completa
* Interface com drag-and-drop para PCP
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
