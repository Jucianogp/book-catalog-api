# 📚 LiterAlura - Catálogo de Livros

Projeto Java desenvolvido como desafio para construir um catálogo de livros interativo via console, consumindo a API [Gutendex](https://gutendex.com) e armazenando dados no PostgreSQL.

---

## 🚀 Tecnologias usadas

- Java 21
- Spring Boot 3.5.4
- Maven
- PostgreSQL 16+
- Jackson (JSON)
- HttpClient (requisições HTTP)
- Spring Data JPA

---

## ⚙️ Funcionalidades

- Buscar livro por título na API Gutendex
- Salvar livros e autores no banco de dados
- Listar todos os livros cadastrados
- Filtrar livros por idioma
- Listar autores
- Consultar autores vivos em determinado ano
- Mostrar quantidade de livros por idioma

---

## 🧱 Como rodar

1. Certifique-se de ter o Java JDK e o Maven instalados.
2. Configure as seguintes variaveis de ambiente com seus dados.
- POSTGRES_DB_PASSWORD 
- POSTGRES_DB_USER 
- DB_HOST
3. Clone o repositório para o seu ambiente local.
4. Navegue até o diretório do projeto no terminal.
5. Execute o comando `./mvnw clean
./mvnw spring-boot:run` para iniciar a aplicação.
6. Siga as instruções apresentadas no console para interagir com o programa.
