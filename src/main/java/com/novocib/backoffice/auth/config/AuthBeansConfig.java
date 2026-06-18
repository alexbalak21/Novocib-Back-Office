package com.novocib.backoffice.auth.config;

@Configuration
public class AuthBeansConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
