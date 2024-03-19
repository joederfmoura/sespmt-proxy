package br.gov.mt.sesp.gerenciamentopatrulha.controllers;

import br.gov.mt.sesp.gerenciamentopatrulha.dtos.PatrulhaDTO;
import br.gov.mt.sesp.gerenciamentopatrulha.dtos.PatrulhaSaveDTO;
import br.gov.mt.sesp.gerenciamentopatrulha.models.Patrulha;
import br.gov.mt.sesp.gerenciamentopatrulha.services.PatrulhaService;
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
 * Controller responsável pelos endpoints referentes à {@link Patrulha}.
 * URI: <b>/patrulhas<b/>
 */
@RestController
@RequestMapping(value = "/patrulhas")
public class PatrulhaController {

    private PatrulhaService patrulhaService;

    @Autowired
    public PatrulhaController(PatrulhaService patrulhaService) {
        this.patrulhaService = patrulhaService;
    }

    /**
     * Endpoint responsável pela listagem de patrulhas.
     * Método: GET
     * URI: /patrulhas
     *
     * @param viatura placa da viatura, utilizado para busca por viatura
     * @param idBairro identificador do bairro, utilizado para busca por bairro
     * @param idPolicial identificador do policial, utilizado para busca por policial
     * @return ResponseEntity<Set<PatrulhaDTO>> Lista de patrulhas encontradas com base nos parâmetros informados
     */
    @RolesAllowed({"VT", "AC", "AD", "AB"})
    @GetMapping
    @ApiOperation(value = "Realiza a listagem de patrulhas")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<Page<PatrulhaDTO>> listar(
            @PageableDefault(sort = "viatura") Pageable pageable,
            @RequestParam(name = "viatura", required = false) String viatura,
            @RequestParam(name = "bairro", required = false) Long idBairro,
            @RequestParam(name = "policial", required = false) Long idPolicial
    ) {
        Patrulha filtro = new Patrulha();
        filtro.setViatura(viatura);

        if (idBairro != null) {
            filtro.incluirBairro(idBairro);
        }

        if (idPolicial != null) {
            filtro.incluirPolicial(idPolicial);
        }

        Page<PatrulhaDTO> listaPatrulhaDTO = patrulhaService.listar(pageable, filtro);

        return ResponseEntity.ok().body(listaPatrulhaDTO);
    }

    /**
     * Endpoint responsável pelo cadastro de uma nova patrulha
     * Método: POST
     * URI: /patrulhas
     *
     * @param patrulhaSaveDTO dados da patrulha para cadastro no formato {@link PatrulhaSaveDTO}
     * @param uriComponentsBuilder construtor de URI, informado automaticamente pelo Spring Boot
     * @return ResponseEntity<PatrulhaDTO> dados da patrulha cadastrada no formato {@link PatrulhaDTO}
     */
    @RolesAllowed("AB")
    @PostMapping
    @ApiOperation(value = "Cria um novo registro de patrulha")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<PatrulhaDTO> criar(
            @RequestBody @Valid PatrulhaSaveDTO patrulhaSaveDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        PatrulhaDTO patrulhaDTO = patrulhaService.salvar(patrulhaSaveDTO);

        URI uri = uriComponentsBuilder.path("/patrulhas/{id}").buildAndExpand(patrulhaDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(patrulhaDTO);
    }

    /**
     * Endpoint responsável pela busca de uma patrulha
     * Método: GET
     * URI: /patrulhas/{id}
     *
     * @param id identificador único da patrulha, utilizado para busca por identificador
     * @return ResponseEntity<PatrulhaDTO> dados da patrulha encontrada no formato {@link PatrulhaDTO}
     */
    @RolesAllowed({"VT", "AC", "AD", "AB"})
    @GetMapping("/{id}")
    @ApiOperation(value = "Busca por uma patrulha")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Recurso não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<PatrulhaDTO> buscar(@PathVariable("id") Long id) {
        PatrulhaDTO patrulhaDTO = patrulhaService.buscar(id);

        return ResponseEntity.ok().body(patrulhaDTO);
    }

    /**
     * Endpoint responsável pela alteração dos dados de uma patrulha
     * Método: PUT
     * URI: /patrulhas/{id}
     *
     * @param id identificador único da patrulha, utilizado para buscar a patrulha que será alterada
     * @return ResponseEntity<PatrulhaDTO> dados da patrulha alterada no formato {@link PatrulhaDTO}
     */
    @RolesAllowed("AB")
    @PutMapping("/{id}")
    @ApiOperation(value = "Altera os dados de uma patrulha")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Recurso não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<PatrulhaDTO> alterar(
            @PathVariable("id") Long id,
            @RequestBody @Valid PatrulhaSaveDTO patrulhaSaveDTO
    ) {
        patrulhaSaveDTO.setId(id);
        PatrulhaDTO patrulhaDTO = patrulhaService.salvar(patrulhaSaveDTO);

        return ResponseEntity.ok().body(patrulhaDTO);
    }
}
