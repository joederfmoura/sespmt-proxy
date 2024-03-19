package br.gov.mt.sesp.registrogeral.services;

import br.gov.mt.sesp.registrogeral.dtos.CidadeDTO;
import br.gov.mt.sesp.registrogeral.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.registrogeral.models.Cidade;
import br.gov.mt.sesp.registrogeral.repositories.CidadeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelas regras negociais referentes à {@link Cidade}
 */
@Service
public class CidadeService {

    private CidadeRepository cidadeRepository;

    private ModelMapper modelMapper;

    @Autowired
    public CidadeService(CidadeRepository cidadeRepository, ModelMapper modelMapper) {
        this.cidadeRepository = cidadeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Lista as cidades com base no filtro informado. Caso o filtro não tenha atributos preenchidos, lista todas as cidades.
     *
     * @param uf UF da cidade, utilizado para busca por UF
     * @param nome nome ou parte do nome da cidade, utilizado para busca por nome
     * @return Page<CidadeDTO> lista de cidades com base no filtro informado
     */
    @Cacheable("cidades")
    public Page<CidadeDTO> listar(Pageable pageable, String uf, String nome) {
        return cidadeRepository.findAll(pageable, uf, nome)
                .map(cidade -> modelMapper.map(cidade, CidadeDTO.class));
    }

    /**
     * Busca uma cidade com base no identificador informado.
     *
     * @param id identificador da cidade
     * @return CidadeDTO dados da cidade encontrada, no formato {@link CidadeDTO}
     */
    @Cacheable("cidades")
    public CidadeDTO buscar(Long id) {
        Cidade cidade = cidadeRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("cidade.naoEncontrada"));

        return modelMapper.map(cidade, CidadeDTO.class);
    }
}
