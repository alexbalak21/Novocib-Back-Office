# Modules Implementation Guide  
### Spring Boot — Maven Multi‑Module Architecture  
### For Copilot Agent (VS Code)

This document defines **exactly** how to create and structure Maven modules inside the  
`novocib-backoffice` project.  
Copilot must follow these rules strictly.

---

# 1. Project Architecture Overview

The project is a **modular monolith** using **Maven multi‑module** layout.

Root structure:

```
novocib-backoffice/
│
├── pom.xml                     ← parent POM (declares modules)
│
├── backoffice-app/             ← main Spring Boot application
│
├── module-stocks/              ← domain module
│
└── module-timetracking/        ← domain module
```

---

# 2. Parent POM (root)

The root `pom.xml` MUST contain:

```xml
<packaging>pom</packaging>

<modules>
    <module>backoffice-app</module>
    <module>module-stocks</module>
    <module>module-timetracking</module>
</modules>
```

This is the **only** POM that contains `<modules>`.

---

# 3. Module Folder Structure (Copilot must generate this)

Each module MUST follow this structure:

```
module-<name>/
│
├── pom.xml
│
└── src/main/java/com/novocib/<name>/
    ├── domain/
    ├── dto/
    ├── mapper/
    ├── repository/
    └── service/
```

Example for `module-stocks`:

```
module-stocks/
│
├── pom.xml
│
└── src/main/java/com/novocib/stocks/
    ├── domain/
    ├── dto/
    ├── mapper/
    ├── repository/
    └── service/
```

---

# 4. Module POM Template (Copilot must use this)

Each module uses this template:

```xml
<project>
    <parent>
        <groupId>com.novocib</groupId>
        <artifactId>novocib-backoffice</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>module-<name></artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
    </dependencies>
</project>
```

Replace `<name>` with the module name.

---

# 5. Main Application Module (backoffice-app)

`backoffice-app` contains:

- Spring Boot entry point
- Controllers
- GraphQL resolvers
- Global configuration
- Security
- Application properties

Structure:

```
backoffice-app/
│
├── pom.xml
│
└── src/main/java/com/novocib/backoffice/
    ├── BackofficeApplication.java
    └── controllers/
```

---

# 6. Importing Modules in Main App

`backoffice-app/pom.xml` MUST include:

```xml
<dependencies>
    <dependency>
        <groupId>com.novocib</groupId>
        <artifactId>module-stocks</artifactId>
    </dependency>

    <dependency>
        <groupId>com.novocib</groupId>
        <artifactId>module-timetracking</artifactId>
    </dependency>
</dependencies>
```

---

# 7. Module Configuration Classes

Each module MUST expose a configuration class.

Example for `module-stocks`:

```java
@Configuration
@ComponentScan("com.novocib.stocks")
public class StocksModuleConfig {}
```

Example for `module-timetracking`:

```java
@Configuration
@ComponentScan("com.novocib.timetracking")
public class TimeTrackingModuleConfig {}
```

Main app imports them:

```java
@SpringBootApplication
@Import({
    StocksModuleConfig.class,
    TimeTrackingModuleConfig.class
})
public class BackofficeApplication {}
```

---

# 8. Rules for Copilot Agent

### ✔ Copilot MUST:
- Create a folder named `module-<name>`
- Create a `pom.xml` inside the module
- Create `src/main/java/com/novocib/<name>/`
- Create subfolders: `domain`, `dto`, `mapper`, `repository`, `service`
- Generate classes with correct package names
- Keep controllers in `backoffice-app`
- Add module dependencies to `backoffice-app/pom.xml`
- Add module entries to root `<modules>` section

### ❌ Copilot MUST NOT:
- Put controllers inside modules
- Put Spring Boot main class inside modules
- Put `application.properties` inside modules
- Use `@SpringBootApplication` inside modules
- Create circular dependencies between modules

---

# 9. Example Module Implementation (Stocks)

```
module-stocks/
└── src/main/java/com/novocib/stocks/
    ├── domain/Stock.java
    ├── dto/CreateStockInput.java
    ├── mapper/StockMapper.java
    ├── repository/StockRepository.java
    └── service/StockService.java
```

---

# 10. Build & Run

Build all modules:

```
mvn clean install
```

Run main app:

```
mvn spring-boot:run -pl backoffice-app
```

---

# 11. Summary

This guide defines:

- Folder structure  
- Module POM templates  
- Main app responsibilities  
- Module responsibilities  
- Copilot Agent rules  

Use this file to generate modules automatically with Copilot in VS Code.

