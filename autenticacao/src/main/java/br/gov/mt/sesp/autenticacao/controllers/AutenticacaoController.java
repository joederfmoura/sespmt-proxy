package br.gov.mt.sesp.autenticacao.controllers;

import br.gov.mt.sesp.autenticacao.dtos.LoginDTO;
import br.gov.mt.sesp.autenticacao.dtos.RefreshDTO;
import br.gov.mt.sesp.autenticacao.services.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller responsável pelos endpoints referentes à Autenticação. URI:
 * <b>/autenticacao<b/>
 */
@RestController
@RequestMapping
public class AutenticacaoController {

  private KeycloakService keycloakService;

  @Autowired
  public AutenticacaoController(KeycloakService keycloakService) {
    this.keycloakService = keycloakService;
  }

  @PostMapping("/keycloak/login")
  public ResponseEntity<String> loginKeycloak(@RequestBody LoginDTO loginDTO) throws IOException, InterruptedException {
    String resposta = keycloakService.login(loginDTO.getUsuario(), loginDTO.getSenha());

    return ResponseEntity.ok(resposta);
  }

  @PostMapping("/keycloak/refresh")
  public ResponseEntity<String> refresh(@RequestBody RefreshDTO refreshDTO) throws IOException, InterruptedException {
    String resposta = keycloakService.refresh(refreshDTO.getRefreshToken());

    return ResponseEntity.ok(resposta);
  }

  @GetMapping("/keycloak/info")
  public ResponseEntity<String> info(@RequestHeader("Authorization") String token) throws IOException, InterruptedException {
    String resposta = keycloakService.userInfo(token);

    return ResponseEntity.ok(resposta);
  }

  @PostMapping("/google/login")
  public ResponseEntity<String> loginGoogle(@RequestHeader("Authorization") String token) throws IOException, InterruptedException, JSONException {
    String resposta = keycloakService.loginGoogle(token);

    return ResponseEntity.ok(resposta);
  }
}