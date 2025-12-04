package com.example.moeda.moedaestudantil.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class PasswordService {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  public String hash(String raw) { return encoder.encode(raw); }
  public boolean matches(String raw, String hashed) { return encoder.matches(raw, hashed); }
}
