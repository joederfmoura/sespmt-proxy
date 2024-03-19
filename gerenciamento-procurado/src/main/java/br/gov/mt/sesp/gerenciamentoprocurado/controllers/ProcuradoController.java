package br.gov.mt.sesp.gerenciamentoprocurado.controllers;

import java.net.URI;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.gov.mt.sesp.gerenciamentoprocurado.dtos.ProcuradoDTO;
import br.gov.mt.sesp.gerenciamentoprocurado.dtos.ProcuradoSaveDTO;
import br.gov.mt.sesp.gerenciamentoprocurado.models.Procurado;
import br.gov.mt.sesp.gerenciamentoprocurado.services.ProcuradoService;

/**
 * Controller responsável pelos endpoints referentes à {@link Procurado}.
 * URI: <b>/procurados<b/>
 */
@RestController
@RequestMapping(value = "/procurados")
public class ProcuradoController {

    private static final Logger LOGGER = LogManager.getLogger(Procurado.class);

    private ProcuradoService procuradoService;

    @Autowired
    public ProcuradoController(ProcuradoService procuradoService) {

        this.procuradoService = procuradoService;

    }

    /**
     * Endpoint responsável pela listagem de procurados.
     * Método: GET
     * URI: /procurados
     *
     * @param caracteristicas caracteristicas do procurado, utilizado para busca por caracteristicas
     * @param idBoletimOcorrencia identificador do boletim de ocorrência, utilizado para busca por id
     * @param idPessoa identificador da pessoa, utilizado para busca por id da pessoa
     * @return ResponseEntity<Set<ProcuradoDTO>> Lista de procurado encontrados com base nos parâmetros informados
     */
    @RolesAllowed({"VT", "AC", "AD"})
    @GetMapping
    @ApiOperation(value = "Realiza a listagem de prcurados")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<Page<ProcuradoDTO>> listar(
            @PageableDefault(sort = "caracteristicas") Pageable pageable,
            @RequestParam(name = "caracteristicas", defaultValue = "", required = false) String caracteristicas,
            @RequestParam(name = "idBoletimOcorrencia", required = false) Long idBoletimOcorrencia,
            @RequestParam(name = "idPessoa", required = false) Long idPessoa
    ) {
        Procurado filtro = new Procurado();
        filtro.setCaracteristicas(caracteristicas);
        filtro.setIdBoletimOcorrencia(idBoletimOcorrencia);
        filtro.setIdPessoa(idPessoa);

        Page<ProcuradoDTO> listaProcuradoDTO = procuradoService.listar(pageable, filtro);

        return ResponseEntity.ok().body(listaProcuradoDTO);
    }

    /**
     * Endpoint responsável pelo cadastro de um novo procurado
     * Método: POST
     * URI: /procurados
     *
     * @param procuradoSaveDTO dados do procurado para cadastro no formato {@link ProcuradoSaveDTO}
     * @param uriComponentsBuilder construtor de URI, informado automaticamente pelo Spring Boot
     * @return ResponseEntity<ProcuradoDTO> dados do procurado cadastrado no formato {@link ProcuradoDTO}
     */
    @RolesAllowed({"AC", "AD"})
    @PostMapping
    @ApiOperation(value = "Realiza o cadastro de um novo procurado")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<ProcuradoDTO> criar(
            @RequestBody @Valid ProcuradoSaveDTO procuradoSaveDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        ProcuradoDTO procuradoDTO = procuradoService.salvar(procuradoSaveDTO);

        URI uri = uriComponentsBuilder.path("/procurados/{id}").buildAndExpand(procuradoDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(procuradoDTO);
    }

    /**
     * Endpoint responsável pela busca de um procurado
     * Método: GET
     * URI: /procurados/{id}
     *
     * @param id identificador único do procurado, utilizado para busca por identificador
     * @return ResponseEntity<ProcuradoDTO> dados do procurado encontrado no formato {@link ProcuradoDTO}
     */
    @RolesAllowed({"VT", "AC", "AD"})
    @GetMapping("/{id}")
    @ApiOperation(value = "Busca por um procurado pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Procurado não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<ProcuradoDTO> buscar(@PathVariable("id") Long id) {
        ProcuradoDTO procuradoDTO = procuradoService.buscar(id);

        LOGGER.info(procuradoDTO);

        return ResponseEntity.ok().body(procuradoDTO);
    }

    /**
     * Endpoint responsável pela alteração dos dados de um procurado
     * Método: PUT
     * URI: /procurados/{id}
     *
     * @param id identificador único do procurado, utilizado para buscar o procurado que será alterado
     * @return ResponseEntity<ProcuradoDTO> dados do procurado alterado no formato {@link ProcuradoDTO}
     */
    @RolesAllowed({"AC", "AD"})
    @PutMapping("/{id}")
    @ApiOperation(value = "Altera os dados de um procurado pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Procurado não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<ProcuradoDTO> alterar(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProcuradoSaveDTO procuradoSaveDTO
    ) {
        procuradoSaveDTO.setId(id);
        ProcuradoDTO procuradoDTO = procuradoService.salvar(procuradoSaveDTO);

        return ResponseEntity.ok().body(procuradoDTO);
    }

    /**
     * Endpoint responsável pela remoção de um procurado
     * Método: DELETE
     * URI: /procurados/{id}
     *
     * @param id identificador único do procurado, utilizado para exclusão por identificador
     * @return ResponseEntity<String> id do procurado removido no formato {@link String}
     */
    @RolesAllowed({"AC", "AD"})
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Exclui um procurado")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Procurado não localizado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<Long> excluir(@PathVariable("id") Long id) {
        procuradoService.excluir(id);

        return ResponseEntity.ok().body(id);
    }
}