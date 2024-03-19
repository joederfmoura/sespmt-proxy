package br.gov.mt.sesp.carteirafuncional.controllers;

import br.gov.mt.sesp.carteirafuncional.dtos.AdministrativoDTO;
import br.gov.mt.sesp.carteirafuncional.dtos.AdministrativoSaveDTO;
import br.gov.mt.sesp.carteirafuncional.models.Administrativo;
import br.gov.mt.sesp.carteirafuncional.services.AdministrativoService;
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
 * Controller responsável pelos endpoints referentes à {@link Administrativo}.
 * URI: <b>/administrativos<b/>
 */
@RestController
@RequestMapping(value = "/administrativos")
public class AdministrativoController {

    private AdministrativoService administrativoService;

    @Autowired
    public AdministrativoController(AdministrativoService administrativoService) {
        this.administrativoService = administrativoService;
    }

    /**
     * Endpoint responsável pela listagem de administrativos.
     * Método: GET
     * URI: /administrativos
     *
     * @param nome nome do administrativo, utilizado para busca por nome
     * @param cpf CPF do administrativo, utilizado para busca por CPF
     * @param matricula matricula do administrativo, utilizado para busca por matricula
     * @param email email do administrativo, utilizado para busca por email
     * @return ResponseEntity<Set<AdministrativoDTO>> Lista de administrativos encontradas com base nos parâmetros informados
     */
    @RolesAllowed({"VT", "AC", "AD", "RH", "PL"})
    @ApiOperation(value = "Retorna uma lista de adminstrativos")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<Page<AdministrativoDTO>> listar(
            @PageableDefault(sort = "nome") Pageable pageable,
            @RequestParam(name = "nome", defaultValue = "", required = false) String nome,
            @RequestParam(name = "cpf", defaultValue = "", required = false) String cpf,
            @RequestParam(name = "matricula", defaultValue = "", required = false) String matricula,
            @RequestParam(name = "email", defaultValue = "", required = false) String email
    ) {
        Administrativo filtro = new Administrativo();
        filtro.setCpf(cpf);
        filtro.setNome(nome);
        filtro.setMatricula(matricula);
        filtro.setEmail(email);

        Page<AdministrativoDTO> listaAdministrativoDTO = administrativoService.listar(pageable, filtro);

        return ResponseEntity.ok().body(listaAdministrativoDTO);
    }

    /**
     * Endpoint responsável pelo cadastro de um novo administrativo
     * Método: POST
     * URI: /administrativos
     *
     * @param administrativoSaveDTO dados do administrativo para cadastro no formato {@link AdministrativoSaveDTO}
     * @param uriComponentsBuilder construtor de URI, informado automaticamente pelo Spring Boot
     * @return ResponseEntity<AdministrativoDTO> dados do administrativo cadastrado no formato {@link AdministrativoDTO}
     */
    @RolesAllowed("RH")
    @PostMapping
    @ApiOperation(value = "Cria um novo administrativo")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<AdministrativoDTO> criar(
            @RequestBody @Valid AdministrativoSaveDTO administrativoSaveDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        AdministrativoDTO administrativoDTO = administrativoService.salvar(administrativoSaveDTO);

        URI uri = uriComponentsBuilder.path("/administrativos/{id}").buildAndExpand(administrativoDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(administrativoDTO);
    }

    /**
     * Endpoint responsável pela busca de um administrativo
     * Método: GET
     * URI: /administrativos/{id}
     *
     * @param id identificador único do administrativo, utilizado para busca por identificador
     * @return ResponseEntity<AdministrativoDTO> dados do administrativo encontrado no formato {@link AdministrativoDTO}
     */
    @RolesAllowed({"VT", "AC", "AD", "RH", "PL"})
    @GetMapping("/{id}")
    @ApiOperation(value = "Busca por um administrativo por seu id")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Administrativo não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<AdministrativoDTO> buscar(@PathVariable("id") Long id) {
        AdministrativoDTO administrativoDTO = administrativoService.buscar(id);

        return ResponseEntity.ok().body(administrativoDTO);
    }

    /**
     * Endpoint responsável pela alteração dos dados de um administrativo
     * Método: PUT
     * URI: /administrativos/{id}
     *
     * @param id identificador único do administrativo, utilizado para buscar o administrativo que será alterado
     * @return ResponseEntity<AdministrativoDTO> dados do administrativo alterado no formato {@link AdministrativoDTO}
     */
    @RolesAllowed("RH")
    @PutMapping("/{id}")
    @ApiOperation(value = "Altera um registro de administrativo")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Recurso não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<AdministrativoDTO> alterar(
            @PathVariable("id") Long id,
            @RequestBody @Valid AdministrativoSaveDTO administrativoSaveDTO
    ) {
        administrativoSaveDTO.setId(id);
        AdministrativoDTO administrativoDTO = administrativoService.salvar(administrativoSaveDTO);

        return ResponseEntity.ok().body(administrativoDTO);
    }
}