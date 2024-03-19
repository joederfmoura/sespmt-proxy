package br.gov.mt.sesp.registrogeral.controllers;

import br.gov.mt.sesp.registrogeral.dtos.BairroDTO;
import br.gov.mt.sesp.registrogeral.models.Bairro;
import br.gov.mt.sesp.registrogeral.services.BairroService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

/**
 * Controller responsável pelos endpoints referentes à {@link Bairro}.
 * URI: <b>/bairros<b/>
 */
@RestController
@RequestMapping(value = "/bairros")
public class BairroController {


  private BairroService bairroService;

  @Autowired
  public BairroController(BairroService bairroService) {
    this.bairroService = bairroService;
  }

  /**
   * Endpoint responsável pela busca de um bairro
   * Método: GET
   * URI: /bairros/{id}
   *
   * @param id identificador único do bairro, utilizado para busca por identificador
   * @return ResponseEntity<BairroDTO> dados do bairro encontrado no formato {@link BairroDTO}
   */
  @RolesAllowed({"TI", "VT", "AC", "AD", "RH", "PL", "CD"})
  @GetMapping("/{id}")
  @ApiOperation(value = "Busca por um bairro")
  @ApiResponses(value = {
          @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
          @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
          @ApiResponse(code = 404, message = "Bairro não localizado"),
          @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
  })
  public ResponseEntity<BairroDTO> buscar(
      @PathVariable("id") Long id
  ) {
    BairroDTO bairroDTO = bairroService.buscar(id);
    return ResponseEntity.ok().body(bairroDTO);
  }
}