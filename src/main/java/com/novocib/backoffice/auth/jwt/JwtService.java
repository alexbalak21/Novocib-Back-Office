package com.novocib.backoffice.auth.jwt;

@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.access-token-expiration-ms}")
  private long accessTokenExpirationMs;

  public String generateAccessToken(UserDetails user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("roles", user.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority).toList())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();
    return username.equals(userDetails.getUsername()) && !claims.getExpiration().before(new Date());
  }
}
