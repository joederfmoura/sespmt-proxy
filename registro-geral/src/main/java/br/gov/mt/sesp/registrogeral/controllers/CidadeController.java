package br.gov.mt.sesp.registrogeral.controllers;

import br.gov.mt.sesp.registrogeral.dtos.BairroDTO;
import br.gov.mt.sesp.registrogeral.dtos.CidadeDTO;
import br.gov.mt.sesp.registrogeral.services.BairroService;
import br.gov.mt.sesp.registrogeral.services.CidadeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

/**
 * Controller responsável pelos endpoints referentes à {@link Cidade}.
 * URI: <b>/cidades<b/>
 */
@RestController
@RequestMapping(value = "/cidades")
public class CidadeController {

  private CidadeService cidadeService;
  private BairroService bairroService;

  @Autowired
  public CidadeController(CidadeService cidadeService, BairroService bairroService) {
    this.cidadeService = cidadeService;
    this.bairroService = bairroService;
  }

  /**
   * Endpoint responsável pela listagem de cidades.
   * Método: GET
   * URI: /cidades
   *
   * @param uf UF da cidade, utilizado para busca por UF
   * @param nome nome ou parte do nome da cidade, utilizado para busca por nome
   * @return ResponseEntity<Page<CidadeDTO>> Lista de cidades encontradas com base nos parâmetros informados
   */
  @RolesAllowed({"TI", "VT", "AC", "AD", "RH", "PL"})
  @GetMapping
  @ApiOperation(value = "Realiza listagem de cidades")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<Page<CidadeDTO>> listar(
          @PageableDefault(sort = "nome") Pageable pageable,
          @RequestParam(name = "uf", defaultValue = "", required = false) String uf,
          @RequestParam(name = "nome", defaultValue = "", required = false) String nome
  ) {
      Page<CidadeDTO> listaCidadesDTO = cidadeService.listar(pageable, uf, nome);

      return ResponseEntity.ok().body(listaCidadesDTO);
  }

  /**
   * Endpoint responsável pela busca de uma cidade
   * Método: GET
   * URI: /cidades/{id}
   * 
   * @param id identificador único da cidade, utilizado para busca por identificador
   * @return ResponseEntity<CidadeDTO> dados da cidade encontrada no formato {@link CidadeDTO}
   */
  @RolesAllowed({"TI", "VT", "AC", "AD", "RH", "PL"})
  @GetMapping("/{id}")
  @ApiOperation(value = "Busca por uma cidade")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 404, message = "Cidade não localizada"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<CidadeDTO> buscar(@PathVariable("id") Long id) {
    CidadeDTO cidadeDTO = cidadeService.buscar(id);

    return ResponseEntity.ok().body(cidadeDTO);
  }

  /**
   * Endpoint responsável pela listagem de bairros.
   * Método: GET
   * URI: /cidades/{idCidade}/bairros
   *
   * @param idCidade identificador da cidade, para busca por cidade
   * @param nome nome ou parte do nome do bairro, utilizado para busca por nome
   * @return ResponseEntity<Page<BairroDTO>> Lista de bairros encontrados com base nos parâmetros informados
   */
  @RolesAllowed({"TI", "VT", "AC", "AD", "RH", "PL"})
  @GetMapping("/{idCidade}/bairros")
  @ApiOperation(value = "Listagem de bairros em uma cidade")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 404, message = "Cidade não localizada"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<Page<BairroDTO>> listar(
      @PageableDefault(sort = "nome") Pageable pageable,
      @PathVariable("idCidade") Long idCidade,
      @RequestParam(name = "nome", defaultValue = "", required = false) String nome
  ) {
    Page<BairroDTO> listaBairrosDTO = bairroService.listar(pageable, idCidade, nome);

    return ResponseEntity.ok().body(listaBairrosDTO);
  }

  /**
   * Endpoint responsável pela busca de um bairro
   * Método: GET
   * URI: /cidades/{idCidade}/bairros/{id}
   *
   * @param idCidade identificador da cidade, para busca por cidade
   * @param id identificador único do bairro, utilizado para busca por identificador
   * @return ResponseEntity<BairroDTO> dados do bairro encontrado no formato {@link BairroDTO}
   */
  @RolesAllowed({"TI", "VT", "AC", "AD", "RH", "PL"})
  @GetMapping("/{idCidade}/bairros/{id}")
  @ApiOperation(value = "Busca por um bairro em um cidade")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 404, message = "Recurso não localizada"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<BairroDTO> buscarBairro(
      @PathVariable("idCidade") Long idCidade,
      @PathVariable("id") Long id
  ) {
    BairroDTO bairroDTO = bairroService.buscar(idCidade, id);

    return ResponseEntity.ok().body(bairroDTO);
  }
}