# SmartOS

A smart device OS simulator built with Spring Boot and Thymeleaf. Users can customize their personal desktop with app shortcuts, subfolders, wallpapers, and themes. Includes a natural language CLI powered by an LLM.

## Run locally

> Copy the example properties file and add your OpenRouter API key before running:
> ```bash
> cp backend/src/main/resources/application-dev.properties.example \
>    backend/src/main/resources/application-dev.properties
> ```
> Then open the file and replace `your-api-key-here` with your key from [openrouter.ai/keys](https://openrouter.ai/keys).

```bash
cd backend
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

The CLI starts automatically in the same terminal. Type commands in natural language:

```
> open minesweeper for Edi
> run simulation
> list users
> add paint to Anna's menu
> exit
```

## Tech stack

- Java 21, Spring Boot 4, Spring Data JPA, Hibernate 7
- Thymeleaf, Bootstrap 5
- H2 (dev), PostgreSQL (prod)
- OpenRouter API (free tier) for LLM tool calling
