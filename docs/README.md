# Novocib Backoffice — Modular Spring Boot + Astro Architecture

## 📌 Overview
This project is a **modular monolith** built with **Spring Boot**, **Astro**, and **PostgreSQL**.  
Each business domain is isolated into its own backend module, while the frontend uses Astro with optional React islands for interactive components.

This architecture is designed for:
- Clean domain separation  
- High performance  
- Maintainability  
- Scalability  
- Modern developer experience  

---

# 🧱 Backend Architecture (Spring Boot Modular Monolith)

```
novocib-backoffice/
│
├── pom.xml                      # Parent POM
│
├── backoffice-app/              # Main Spring Boot application
│   ├── src/main/java/com.novocib.app/
│   │   ├── controller/          # REST controllers
│   │   ├── config/              # Security, CORS, Jackson, etc.
│   │   └── BackofficeApplication.java
│   └── pom.xml
│
├── module-stocks/
│   ├── src/main/java/com.novocib.stocks/
│   │   ├── domain/              # Entities
│   │   ├── repository/          # JPA repositories
│   │   ├── service/             # Business logic
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── mapper/              # MapStruct or manual mappers
│   │   └── config/              # Module configuration
│   └── pom.xml
│
├── module-invoices/
│   └── same structure...
│
├── module-orders/
│   └── same structure...
│
└── module-time-tracking/
    └── same structure...
```

---

# 🧩 Backend Modules

### `module-stocks`
- Product stock management  
- Stock movements  
- Inventory operations  

### `module-invoices`
- Client management  
- Invoice creation  
- Payment tracking  

### `module-orders`
- Order creation  
- Order workflow  
- Order validation  

### `module-time-tracking`
- Employee time logs  
- Project time allocation  
- Reporting  

---

# 🏛️ Main Application Responsibilities (`backoffice-app`)
- Exposes REST controllers  
- Centralized configuration (security, CORS, Jackson)  
- Imports module configurations  
- Provides API endpoints consumed by Astro  

---

# 🗄️ Database — PostgreSQL

The project uses **PostgreSQL** as the main relational database.

### Example `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/novocib
    username: novocib
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### Recommended local setup:
- Install PostgreSQL  
- Create database: `novocib`  
- Create user: `novocib`  
- Grant privileges  

---

# 🔌 Module Configuration Example

Each module exposes its beans via a config class:

```java
@Configuration
@ComponentScan("com.novocib.stocks")
public class StocksModuleConfig {}
```

Main app imports all modules:

```java
@SpringBootApplication
@Import({
    StocksModuleConfig.class,
    InvoicesModuleConfig.class,
    OrdersModuleConfig.class,
    TimeTrackingModuleConfig.class
})
public class BackofficeApplication {}
```

---

# 🧭 Dependency Rules

### ✔ Allowed
- `backoffice-app` → depends on all modules  
- Each module → depends on Spring Boot + JPA  

### ❌ Not allowed
- Modules depending on each other  
- Controllers inside modules  
- Shared “god” module with mixed logic  

---

# 🌐 Frontend Architecture (Astro + React Islands)

```
frontend/
│
├── src/
│   ├── pages/
│   │   ├── index.astro
│   │   ├── stocks/
│   │   │   └── index.astro
│   │   ├── invoices/
│   │   │   └── index.astro
│   │   ├── orders/
│   │   │   └── index.astro
│   │   └── time-tracking/
│   │       └── index.astro
│   │
│   ├── components/
│   │   ├── react/
│   │   │   ├── StockTable.jsx
│   │   │   ├── InvoiceForm.jsx
│   │   │   └── OrderChart.jsx
│   │   └── ui/
│   │       ├── Sidebar.astro
│   │       ├── Navbar.astro
│   │       └── Card.astro
│   │
│   ├── layouts/
│   │   └── BackofficeLayout.astro
│   │
│   └── lib/
│       └── api.js   # fetch wrappers for Spring Boot
│
└── astro.config.mjs
```

---

# 🔌 Example: Fetching Spring Boot Data in Astro

### Server‑side (fastest)

```astro
---
const res = await fetch("http://localhost:8080/api/stocks");
const stocks = await res.json();
---
```

### Client‑side React island

```jsx
import { useEffect, useState } from "react";

export default function StockTable() {
  const [stocks, setStocks] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/stocks")
      .then(r => r.json())
      .then(setStocks);
  }, []);

  return (
    <table>
      {stocks.map(s => (
        <tr key={s.id}>
          <td>{s.name}</td>
          <td>{s.quantity}</td>
        </tr>
      ))}
    </table>
  );
}
```

---

# 🚀 Build & Run

### Backend (Spring Boot)
```
mvn clean install
mvn spring-boot:run -pl backoffice-app
```

### Frontend (Astro)
```
npm install
npm run dev
```

---

# 📄 License
Internal Novocib project — not for public distribution.

