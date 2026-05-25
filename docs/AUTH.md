# Authentication Architecture  
### Spring Boot (JWT Access + Refresh Tokens) + Astro (React Islands + Zustand)

This document explains how to implement a secure authentication system using:

- Spring Boot (JWT access + refresh tokens)

# Authentication Architecture

### Spring Boot (JWT Access + Refresh Tokens) + Astro (React Islands + Zustand)

This document explains how to implement a secure authentication system using:

- Spring Boot (JWT access + refresh tokens)
- HttpOnly cookies for refresh tokens
- Astro frontend
- React Islands
- Zustand for client-side auth state
- Auto-refresh of expired access tokens

---

## 1. Authentication Flow Overview

### Access Token

- Short-lived (5–15 minutes)
- Sent in `Authorization: Bearer <token>`
- Stored in **Zustand** (memory)
- Optionally synced to `localStorage`

### Refresh Token

- Long-lived (7–30 days)
- Stored in **HttpOnly Secure cookie**
- Never accessible from JavaScript
- Automatically sent by browser

### Flow

1. User logs in with email/password.
2. Backend validates credentials.
3. Backend returns:
   - `accessToken` in JSON
   - `refreshToken` in HttpOnly cookie
4. Frontend stores access token in Zustand.
5. When access token expires:
   - Frontend calls `/auth/refresh`
   - Backend validates refresh token
   - Backend returns new access token
   - Zustand updates token

---

## 2. Spring Boot Implementation

### 2.1 Dependencies (Maven)

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
  </dependency>
</dependencies>
```

### 2.2 JWT Service

```java
@Service
public class JwtService {
    private final Key key = Keys.hmacShaKeyFor(
        "your-very-long-secret-key-change-me-1234567890".getBytes(StandardCharsets.UTF_8)
    );

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(Date.from(Instant.now().plusSeconds(900)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(Date.from(Instant.now().plusSeconds(60L * 60 * 24 * 7)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

### 2.3 Login + Refresh Endpoints

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request,
                                                     HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String username = auth.getName();
        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue("refreshToken") String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        String newAccessToken = jwtService.generateAccessToken(username);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
```

### 2.4 Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/refresh").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

## 3. Astro Frontend Implementation

### 3.1 Zustand Store

```typescript
import { create } from "zustand";

interface AuthState {
  accessToken: string | null;
  setAccessToken: (token: string | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  setAccessToken: (token) => set({ accessToken: token }),
  logout: () => set({ accessToken: null }),
}));
```

### 3.2 Login + Refresh API

```typescript
import { useAuthStore } from "../state/auth.store";

export async function login(email: string, password: string) {
  const res = await fetch("/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ email, password }),
  });

  if (!res.ok) throw new Error("Invalid credentials");

  const data = await res.json();
  useAuthStore.getState().setAccessToken(data.accessToken);
}

export async function refreshToken() {
  const res = await fetch("/auth/refresh", {
    method: "POST",
    credentials: "include",
  });

  if (!res.ok) {
    useAuthStore.getState().logout();
    throw new Error("Refresh failed");
  }

  const data = await res.json();
  useAuthStore.getState().setAccessToken(data.accessToken);
}
```

### 3.3 GraphQL Client with Auto-Refresh

```typescript
import { GraphQLClient } from "graphql-request";
import { useAuthStore } from "@/modules/auth/state/auth.store";
import { refreshToken } from "@/modules/auth/services/auth.api";

const graphqlUrl =
  import.meta.env.DEV ? "/graphql" : import.meta.env.PUBLIC_API_URL;

export const client = new GraphQLClient(graphqlUrl, {
  headers: () => {
    const token = useAuthStore.getState().accessToken;
    return token ? { Authorization: `Bearer ${token}` } : {};
  },
});

export async function gqlRequest<T = any>(query: string, variables?: any): Promise<T> {
  try {
    return await client.request<T>(query, variables);
  } catch (err: any) {
    const status = err?.response?.status ?? err?.response?.statusCode;
    if (status === 401) {
      await refreshToken();
      return await client.request<T>(query, variables);
    }
    throw err;
  }
}
```

### 3.4 Login React Component

```typescript
import { useState } from "react";
import { login } from "@/modules/auth/services/auth.api";

export default function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      await login(email, password);
    } catch {
      setError("Invalid credentials");
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <input value={email} onChange={(e) => setEmail(e.target.value)} />
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      <button>Login</button>
      {error && <p>{error}</p>}
    </form>
  );
}
```

### 3.5 Protected Component Example

```typescript
import { useAuthStore } from "@/modules/auth/state/auth.store";

export default function Dashboard() {
  const token = useAuthStore((s) => s.accessToken);

  if (!token) return <div>Please login</div>;

  return <div>Welcome to your dashboard</div>;
}
```

## 4. Environment Variables

```env
PUBLIC_API_URL=https://your-backend.com/graphql
```
