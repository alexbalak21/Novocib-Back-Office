# Project Implementation Guide — NOVOCIB BACK OFFICE

This document defines the architecture, conventions, and implementation rules
that VS Code Copilot must follow when generating or modifying code in this project.

---

# 1. Project Type

This is a single Spring Boot application (NOT a multi‑module Maven project).

All code lives under:

src/main/java/com/novocib/backoffice

The application entry point is:

com.novocib.backoffice.Application

---

# 2. Internal Module Structure

The project contains internal modules, implemented as packages inside the application:

com.novocib.backoffice
 ├── stocks
 │    ├── domain
 │    ├── repository
 │    ├── service
 │    ├── controller
 │    └── graphql
 ├── timetracking
 │    ├── domain
 │    ├── repository
 │    ├── service
 │    ├── controller
 │    └── graphql
 └── shared
      ├── config
      ├── exceptions
      └── utils

Copilot must always respect this structure.

---

# 3. Domain Layer Rules

- Entities go in module/domain
- Use jakarta.persistence.*
- Entities must include:
  - @Entity
  - @Table
  - @Id
  - @GeneratedValue
  - Protected no‑args constructor
  - Public constructor for required fields

Example:

@Entity
@Table(name = "time_entries")
public class TimeEntry { ... }

---

# 4. Repository Layer Rules

- Repositories go in module/repository
- Must extend JpaRepository<Entity, Long>
- @Repository annotation is optional

Example:

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {}

---

# 5. Service Layer Rules

- Services go in module/service
- Must be annotated with @Service
- Must use constructor injection
- Must call repositories, not controllers
- Must contain business logic

Example:

@Service
public class TimeEntryService { ... }

---

# 6. Controller Layer Rules (REST)

- Controllers go in module/controller
- Must be annotated with:
  - @RestController
  - @RequestMapping("/module-name")
- Must return DTOs, not entities
- Must call services, not repositories

Example:

@RestController
@RequestMapping("/timetracking")
public class TimeEntryController { ... }

---

# 7. GraphQL Layer Rules

- GraphQL resolvers go in module/graphql
- Use:
  - @QueryMapping
  - @MutationMapping
  - @SchemaMapping
- Must call services

Example:

@QueryMapping
public List<TimeEntry> timeEntries() { ... }

---

# 8. Shared Module Rules

The shared/ package contains:

- global configuration (@Configuration)
- exception handlers (@ControllerAdvice)
- utilities
- constants

---

# 9. Application Configuration

The file src/main/resources/application.properties must contain:

spring.datasource.url=jdbc:postgresql://localhost:5432/novocib
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

---

# 10. Copilot Behavior Rules

Copilot must:

- Always generate code inside the correct module package
- Never create new Maven modules
- Never modify the project structure outside this document
- Always follow the flow:
  domain → repository → service → controller → graphql
- Always use constructor injection
- Always generate clean, readable, maintainable code

Copilot must NOT:

- Create multi-module Maven structures
- Place code outside com.novocib.backoffice
- Use field injection (@Autowired on fields)
- Generate unused or empty classes

---

# 11. Naming Conventions

- Entities: EntityName
- Repositories: EntityNameRepository
- Services: EntityNameService
- Controllers: EntityNameController
- GraphQL resolvers: EntityNameGraphQL

---

# 12. Example Module Structure (TimeTracking)

timetracking/
 ├── domain/TimeEntry.java
 ├── repository/TimeEntryRepository.java
 ├── service/TimeEntryService.java
 ├── controller/TimeEntryController.java
 └── graphql/TimeEntryGraphQL.java

---

# 13. Goal

This document ensures that Copilot implements the project consistently,
cleanly, and according to the architecture defined here.
