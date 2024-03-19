package br.gov.mt.sesp.registrogeral.controllers;

import br.gov.mt.sesp.registrogeral.dtos.PessoaDTO;
import br.gov.mt.sesp.registrogeral.dtos.PessoaSaveDTO;
import br.gov.mt.sesp.registrogeral.models.Pessoa;
import br.gov.mt.sesp.registrogeral.services.PessoaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;

/**
 * Controller responsável pelos endpoints referentes à {@link Pessoa}.
 * URI: <b>/pessoas<b/>
 */
@RestController
@RequestMapping(value = "/pessoas")
public class PessoaController {

  private PessoaService pessoaService;

  @Autowired
  public PessoaController(PessoaService pessoaService) {
    this.pessoaService = pessoaService;
  }

  /**
   * Endpoint responsável pela listagem de pessoas.
   * Método: GET
   * URI: /pessoas
   *
   * @param cpf CPF da pessoa, utilizado para busca por CPF
   * @param nome nome da pessoa, utilizado para busca por nome
   * @param nomeMae Nome da mãe da pessoa, utilizado para busca por nome da mãe
   * @param email email da pessoa, utilizado para busca por email
   * @return ResponseEntity<Page<PessoaDTO>> Lista de pessoas encontradas com base nos parâmetros informados
   */
  @RolesAllowed({"TI", "VT", "AC", "AD", "RH", "PL", "CD"})
  @GetMapping
  @ApiOperation(value = "Realiza a listagem de pessoas")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<Page<PessoaDTO>> listar(
          @PageableDefault(sort = "nome") Pageable pageable,
          @RequestParam(name = "cpf", required = false) String cpf,
          @RequestParam(name = "nome", defaultValue = "", required = false) String nome,
          @RequestParam(name = "nome-mae", defaultValue = "", required = false) String nomeMae,
          @RequestParam(name = "email", defaultValue = "", required = false) String email
  ) {
      Page<PessoaDTO> listaPessoaDTO = pessoaService.listar(pageable, cpf, nome, nomeMae, email);

      return ResponseEntity.ok().body(listaPessoaDTO);
  }
  
  /**
   * Endpoint responsável pelo cadastro de uma nova pessoa
   * Método: POST
   * URI: /pessoas
   * 
   * @param pessoaSaveDTO dados da pessoa para cadastro no formato {@link PessoaSaveDTO}
   * @param uriComponentsBuilder construtor de URI, informado automaticamente pelo Spring Boot
   * @return ResponseEntity<PessoaDTO> dados da pessoa cadastrada no formato {@link PessoaDTO}
   */
  @RolesAllowed({"TI", "PL"})
  @PostMapping
  @ApiOperation(value = "Realiza o registro de uma nova pessoa")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<PessoaDTO> criar(
    @RequestBody @Valid PessoaSaveDTO pessoaSaveDTO, 
    UriComponentsBuilder uriComponentsBuilder
  ) {
    PessoaDTO pessoaDTO = pessoaService.salvar(pessoaSaveDTO);

    URI uri = uriComponentsBuilder.path("/pessoas/{id}").buildAndExpand(pessoaDTO.getId()).toUri();

    return ResponseEntity.created(uri).body(pessoaDTO);
  }

  /**
   * Endpoint responsável pela busca de uma pessoa
   * Método: GET
   * URI: /pessoas/{id}
   * 
   * @param id identificador único da pessoa, utilizado para busca por identificador
   * @return ResponseEntity<PessoaDTO> dados da pessoa encontrada no formato {@link PessoaDTO}
   */
  @RolesAllowed({"TI", "VT", "AC", "AD", "RH", "PL", "CD"})
  @GetMapping("/{id}")
  @ApiOperation(value = "Busca uma pessoa pelo seu ID")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 404, message = "Pessoa não localizada"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<PessoaDTO> buscar(@PathVariable("id") Long id) {
    PessoaDTO pessoaDTO = pessoaService.buscar(id);

    return ResponseEntity.ok().body(pessoaDTO);
  }

  /**
   * Endpoint responsável pela alteração dos dados de uma pessoa
   * Método: PUT
   * URI: /pessoas/{id}
   * 
   * @param id identificador único da pessoa, utilizado para buscar a pessoa que será alterada
   * @return ResponseEntity<PessoaDTO> dados da pessoa alterada no formato {@link PessoaDTO}
   */
  @RolesAllowed({"TI", "PL"})
  @PutMapping("/{id}")
  @ApiOperation(value = "Altera os dados de uma pessoa")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 404, message = "Pessoa não localizada"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<PessoaDTO> alterar(
    @PathVariable("id") Long id,
    @RequestBody @Valid PessoaSaveDTO pessoaSaveDTO
  ) {
    pessoaSaveDTO.setId(id);
    PessoaDTO pessoaDTO = pessoaService.salvar(pessoaSaveDTO);

    return ResponseEntity.ok().body(pessoaDTO);
  }
}