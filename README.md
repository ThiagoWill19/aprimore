# Aprimore — Gerador de Ordem de Serviço para Clicherias e facarias

Aplicação web para criação e gerenciamento de **ordens de serviço** voltada para empresas de **clicheria e matrizes de corte e vinco**.

O projeto nasce da vivência prática no setor e da necessidade de uma solução **simples, específica e alinhada à realidade da fábrica**, evitando erros de informação, retrabalho e perda de histórico técnico.

---

## 📌 Contexto

Empresas desse nicho normalmente utilizam papel, planilhas ou sistemas genéricos para controlar ordens de serviço.  
Isso costuma gerar problemas como:

- Informações incompletas ou inconsistentes  
- Retrabalho por erro de comunicação  
- Dificuldade em rastrear o status da OS  
- Falta de histórico técnico por cliente  

O **Aprimore** surge para atacar esse problema: a **ordem de serviço**, tratando-a como o coração do processo produtivo.

---

## 🎯 Objetivo do Projeto

- Padronizar a criação de ordens de serviço  
- Centralizar informações técnicas em um único lugar  
- Reduzir erros operacionais e retrabalho  
- Servir como base para futuras funcionalidades, como controle de produção  

O desenvolvimento é incremental, validado com usuários reais do setor.

---

## ⚙️ Funcionalidades em Desenvolvimento

- Cadastro de clientes, máquinas e acesso de usuários
- Criação de ordens de serviço  
- Visualização e listagem de ordens de serviço  
- Estrutura inicial para evolução de status da OS  

> ⚠️ O projeto está em desenvolvimento ativo (MVP). Novas funcionalidades serão adicionadas gradualmente.

---

## 🛠️ Tecnologias Utilizadas

- Java 17  
- Spring Boot  
- Spring MVC  
- Spring Data JPA  
- Thymeleaf  
- Bean Validation (Jakarta Validation)  
- Spring Security  
- Lombok  
- ModelMapper  
- Spring Mail  
- Maven  
- MySQL  

---

## 🧱 Arquitetura

A aplicação utiliza uma arquitetura **monolítica MVC**, organizada por módulos de negócio.

### Principais camadas

- **Controller** — Entrada HTTP e validação inicial  
- **Service** — Regras de negócio  
- **Domain** — Entidades e regras do domínio  
- **Repository** — Acesso a dados  

A escolha do monólito visa simplicidade, facilidade de manutenção e validação rápida do domínio antes de evoluções mais complexas.

---

## ▶️ Como Executar o Projeto

### Pré-requisitos

- Java 17 ou superior  
- Maven  
- MySQL  

---

### 🔧 Configuração de Variáveis de Ambiente

O projeto utiliza variáveis de ambiente para configurar acesso ao banco de dados e envio de e-mails.

Configure as seguintes variáveis antes de executar a aplicação:

#### Banco de Dados

- `DB_NAME` → Nome do banco de dados  
- `DB_USER` → Usuário do banco  
- `DB_PASS` → Senha do banco  

#### E-mail (SMTP)

- `SMTP_HOST` → Servidor SMTP  
- `SMTP_PORT` → Porta do servidor SMTP  
- `EMAIL` → E-mail remetente  
- `SMTP_PASS` → Senha do e-mail  

Essas variáveis são utilizadas no arquivo `application.properties`.

---

### 🚀 Executando a Aplicação

```bash
git clone https://github.com/seu-usuario/aprimore.git
cd aprimore
mvn spring-boot:run
```

A aplicação está disponível em **http://localhost:8080**

---

## 🚧 Status do Projeto

🚀 **Em desenvolvimento (MVP)**

### Próximos passos planejados

- Evolução do modelo de Ordem de Serviço  
- Implementação de status e fluxo de produção  
- Ajustes baseados em feedback de usuários reais  
- Controle básico de produção  

---

## 📐 Decisões Técnicas

- Arquitetura monolítica MVC para facilitar evolução e validação do domínio  
- Uso de Bean Validation para garantir consistência dos dados  
- Lombok para redução de boilerplate  
- ModelMapper para conversão entre entidades e DTOs  
- Spring Security preparado para controle de acesso futuro  

## 📬 Contato

Projeto desenvolvido por **Thiago**.  
Sugestões, feedbacks e ideias são bem-vindos.

