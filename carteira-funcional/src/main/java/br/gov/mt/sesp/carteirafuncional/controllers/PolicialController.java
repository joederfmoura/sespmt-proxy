package br.gov.mt.sesp.carteirafuncional.controllers;

import br.gov.mt.sesp.carteirafuncional.dtos.PolicialDTO;
import br.gov.mt.sesp.carteirafuncional.dtos.PolicialSaveDTO;
import br.gov.mt.sesp.carteirafuncional.models.Policial;
import br.gov.mt.sesp.carteirafuncional.services.PolicialService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Controller responsável pelos endpoints referentes à {@link Policial}.
 * URI: <b>/policiais<b/>
 */
@RestController
@RequestMapping(value = "/policiais")
public class PolicialController {

    private static final Logger LOGGER = LogManager.getLogger(PolicialController.class);
    private PolicialService policialService;

    @Autowired
    public PolicialController(PolicialService policialService) {
        this.policialService = policialService;
    }

    /**
     * Endpoint responsável pela listagem de policiais.
     * Método: GET
     * URI: /policiais
     *
     * @param nome nome do policial, utilizado para busca por nome
     * @param cpf CPF do policial, utilizado para busca por CPF
     * @param matricula matricula do policial, utilizado para busca por matricula
     * @param email email do policial, utilizado para busca por email
     * @return ResponseEntity<Set<PolicialDTO>> Lista de policiais encontradas com base nos parâmetros informados
     */
    @RolesAllowed({"VT", "AC", "AD", "RH", "PL"})
    @GetMapping
    @ApiOperation(value = "Realiza a listagem de policias")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<Page<PolicialDTO>> listar(
            @PageableDefault(sort = "nome") Pageable pageable,
            @RequestParam(name = "nome", defaultValue = "", required = false) String nome,
            @RequestParam(name = "cpf", defaultValue = "", required = false) String cpf,
            @RequestParam(name = "matricula", defaultValue = "", required = false) String matricula,
            @RequestParam(name = "email", defaultValue = "", required = false) String email
    ) {
        Policial filtro = new Policial();
        filtro.setCpf(cpf);
        filtro.setNome(nome);
        filtro.setMatricula(matricula);
        filtro.setEmail(email);

        Page<PolicialDTO> listaPolicialDTO = policialService.listar(pageable, filtro);

        return ResponseEntity.ok().body(listaPolicialDTO);
    }

    /**
     * Endpoint responsável pelo cadastro de um novo policial
     * Método: POST
     * URI: /policiais
     *
     * @param policialSaveDTO dados do policial para cadastro no formato {@link PolicialSaveDTO}
     * @param uriComponentsBuilder construtor de URI, informado automaticamente pelo Spring Boot
     * @return ResponseEntity<PolicialDTO> dados do policial cadastrado no formato {@link PolicialDTO}
     */
    @RolesAllowed("RH")
    @PostMapping
    @ApiOperation(value = "Cria um novo registro de policial")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<PolicialDTO> criar(
            @RequestBody @Valid PolicialSaveDTO policialSaveDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        PolicialDTO policialDTO = policialService.salvar(policialSaveDTO);

        URI uri = uriComponentsBuilder.path("/policiais/{id}").buildAndExpand(policialDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(policialDTO);
    }

    /**
     * Endpoint responsável pela busca de um policial
     * Método: GET
     * URI: /policiais/{id}
     *
     * @param id identificador único do policial, utilizado para busca por identificador
     * @return ResponseEntity<PolicialDTO> dados do policial encontrado no formato {@link PolicialDTO}
     */
    @RolesAllowed({"VT", "AC", "AD", "RH", "PL"})
    @GetMapping("/{id}")
    @ApiOperation(value = "Busca por um policial pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Policial não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<PolicialDTO> buscar(@PathVariable("id") Long id) {
        PolicialDTO policialDTO = policialService.buscar(id);
        return ResponseEntity.ok().body(policialDTO);
    }

    /**
     * Endpoint responsável pela alteração dos dados de um policial
     * Método: PUT
     * URI: /policiais/{id}
     *
     * @param id identificador único do policial, utilizado para buscar o policial que será alterado
     * @return ResponseEntity<PolicialDTO> dados do policial alterado no formato {@link PolicialDTO}
     */
    @RolesAllowed("RH")
    @PutMapping("/{id}")
    @ApiOperation(value = "Altera o registro de um policial")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Recurso não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<PolicialDTO> alterar(
            @PathVariable("id") Long id,
            @RequestBody @Valid PolicialSaveDTO policialSaveDTO
    ) {
        policialSaveDTO.setId(id);
        PolicialDTO policialDTO = policialService.salvar(policialSaveDTO);

        return ResponseEntity.ok().body(policialDTO);
    }
}