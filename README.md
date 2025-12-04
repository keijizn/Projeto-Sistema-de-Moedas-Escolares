# 💰 Moedas Escolares – Sistema de Moeda Estudantil

O **Sistema de Moeda Estudantil – Moedas Escolares** foi criado para valorizar o desempenho dos estudantes, tornando o reconhecimento do mérito algo visível, moderno e recompensador.

Professores distribuem moedas virtuais como incentivo, e alunos podem trocá-las por benefícios oferecidos pelas empresas parceiras.

Principais objetivos do sistema:

- Motivação dos alunos por recompensas reais  
- Transparência no acompanhamento de méritos  
- Conexão inovadora entre escolas, professores, empresas e estudantes  
- Organização e automação de processos educacionais  

---

## 🎯 Funcionalidades Principais

### 👨‍🎓 Cadastro de Alunos
Alunos registram nome, CPF, email e curso para participarem do programa.

### 👩‍🏫 Cadastro de Professores
Professores são responsáveis por distribuir moedas e acompanhar o histórico dos alunos.

### 🏢 Cadastro de Empresas Parceiras
Empresas podem criar benefícios como produtos, serviços, cupons e descontos.

### 💸 Distribuição de Moedas
Professores recebem um saldo de moedas para distribuir de acordo com o desempenho dos estudantes.

### ✉️ Notificações Automáticas por E-mail
Integração com **EmailJS** e **JavaMailSender** para envio de:

- Notificações de recebimento de moedas  
- Confirmação de resgate de benefícios  
- E-mails de redefinição de senha  

### 🎁 Resgate de Benefícios
Alunos podem trocar moedas acumuladas por vantagens.  
Ao resgatar, recebem um **código de uso por e-mail**.

### 🔐 Autenticação e Perfis

Perfis disponíveis:

- Aluno  
- Professor  
- Empresa Parceira  

Recursos de segurança:

- Login por e-mail e senha  
- Recuperação de senha via e-mail  
- Geração de nova senha automática  
- Integração com **Spring Security**  

---

## 🛠️ Tecnologias Utilizadas

### 🔧 Backend – Spring Boot

- Java 17+  
- Spring Boot 3.3.4  
- Spring Data JPA  
- Spring Security  
- PostgreSQL  
- Maven  
- JavaMailSender  
- EmailJS (integração via API)  

### 🌐 Frontend – HTML/CSS/JS

- HTML5  
- CSS3 (layout com glassmorphism em azul-turquesa)  
- JavaScript (vanilla)  
- Consumo de API REST via `fetch`  

---

## 🏛️ Arquitetura do Sistema

O backend segue boas práticas de arquitetura em camadas:

**Controller → Service → Repository → Domain (Model) → DTO**

### Camadas

- **api (Controller)**  
  Endpoints REST para autenticação, cadastro, distribuição de moedas, resgates, etc.

- **service**  
  Contém as regras de negócio (envio de e-mails, reset de senha, cálculos de moedas, validações).

- **repo**  
  Interfaces `Repository` usando Spring Data JPA para acesso ao banco de dados.

- **domain (model)**  
  Entidades principais do sistema, como:
  - Aluno  
  - Professor  
  - Empresa Parceira  
  - Benefício  
  - Menu  
  - Evento / Transações de moedas  

- **dto**  
  Objetos de transferência de dados para entrada/saída da API.

- **security**  
  Configurações de autenticação e autorização (Spring Security).

---

## ⚙️ Instalação e Execução

### ✔️ Pré-requisitos

- Java 17 ou superior  
- Maven 3.8+  
- PostgreSQL configurado (banco `moeda`)  
- Git instalado  
- Navegador moderno  

### 📥 Clonar o Repositório

git clone https://github.com/keijizn/Moedas-Escolares.git
cd Moedas-Escolares

### ▶️ Executar o Backend (Spring Boot)
cd backend

mvn spring-boot:run

### 📁 Estrutura de Pastas

Moedas-Escolares/
│
├── backend/
│   └── src/main/java/com/example/moeda/moedaestudantil/
│       ├── api/        # Controllers (Auth, Aluno, Professor, Empresa, etc)
│       ├── service/    # Regras de negócio, envio de e-mails, reset de senha
│       ├── repo/       # Repositórios JPA
│       ├── domain/     # Entidades do sistema
│       ├── security/   # Configurações de segurança
│       └── dto/        # Data Transfer Objects
│
│   └── src/main/resources/
│       └── application.properties
│
│   └── pom.xml
│
├── frontend/
│   ├── assets/
│   ├── css/
│   ├── js/
│   └── *.html   # Telas de login, aluno, professor, empresa, etc.
│
└── README.md

