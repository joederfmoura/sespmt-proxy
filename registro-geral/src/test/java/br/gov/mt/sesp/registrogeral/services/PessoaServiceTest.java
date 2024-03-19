package br.gov.mt.sesp.registrogeral.services;


import br.gov.mt.sesp.registrogeral.dtos.PessoaDTO;
import br.gov.mt.sesp.registrogeral.dtos.PessoaSaveDTO;
import br.gov.mt.sesp.registrogeral.exceptions.NegocioException;
import br.gov.mt.sesp.registrogeral.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.registrogeral.models.Bairro;
import br.gov.mt.sesp.registrogeral.models.Cidade;
import br.gov.mt.sesp.registrogeral.models.Pessoa;
import br.gov.mt.sesp.registrogeral.repositories.BairroRepository;
import br.gov.mt.sesp.registrogeral.repositories.PessoaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

  @InjectMocks
  private PessoaService sut;
  @Mock
  private BairroRepository bairroRepository;
  @Mock
  private ModelMapper modelMapper;
  @Mock
  private PessoaRepository pessoaRepository;

  private static PessoaSaveDTO umaPessoaSaveDTO() {
    var pessoaSaveDTOMock = new PessoaSaveDTO();
    pessoaSaveDTOMock.setNome("Francisco Miranda Araújo");
    pessoaSaveDTOMock.setNomeMae("Letícia Miranda Nascimento");
    pessoaSaveDTOMock.setDataNascimento("29/01/1989");
    pessoaSaveDTOMock.setTelefones("(65) 99069-9417");
    pessoaSaveDTOMock.setEmail("francisco71@gmail.com");
    pessoaSaveDTOMock.setIdBairro(1L);
    pessoaSaveDTOMock.setComplemento("");
    pessoaSaveDTOMock.setNumero("60");
    pessoaSaveDTOMock.setRua("Rua da Samambaia (Lot V Régia)");
    pessoaSaveDTOMock.setCep("78132-015");
    pessoaSaveDTOMock.setCpf("331.284.071-69");
    return pessoaSaveDTOMock;
  }

  private static Pessoa umaPessoa() {
    var pessoaMock = new Pessoa();
    pessoaMock.setNome("Francisco Miranda Araújo");
    pessoaMock.setNomeMae("Letícia Miranda Nascimento");
    pessoaMock.setDataNascimento(LocalDate.of(1989, 1, 29));
    pessoaMock.setTelefones("(65) 99069-9417");
    pessoaMock.setEmail("francisco71@gmail.com");
    pessoaMock.setComplemento("");
    pessoaMock.setNumero("60");
    pessoaMock.setRua("Rua da Samambaia (Lot V Régia)");
    pessoaMock.setCep("78132-015");
    pessoaMock.setCpf("331.284.071-69");
    pessoaMock.setBairro(umBairro());
    return pessoaMock;
  }

  private static Bairro umBairro() {
    var bairro = new Bairro();
    bairro.setId(1L);
    bairro.setNome("Altos da Glória");
    var cidade = new Cidade();
    cidade.setId(1L);
    cidade.setNome("Cuiabá");
    cidade.setUf("MT");
    bairro.setCidade(cidade);
    return bairro;
  }

  private static PessoaDTO umaPessoaDTO() {
    var pessoaDTOMock = new PessoaDTO();
    pessoaDTOMock.setNome("Francisco Miranda Araújo");
    pessoaDTOMock.setNomeMae("Letícia Miranda Nascimento");
    pessoaDTOMock.setDataNascimento("29/01/1989");
    pessoaDTOMock.setTelefones("(65) 99069-9417");
    pessoaDTOMock.setEmail("francisco71@gmail.com");
    pessoaDTOMock.setComplemento("");
    pessoaDTOMock.setNumero("60");
    pessoaDTOMock.setRua("Rua da Samambaia (Lot V Régia)");
    pessoaDTOMock.setCep("78132-015");
    pessoaDTOMock.setCpf("331.284.071-69");
    var bairro = umBairro();
    pessoaDTOMock.setBairro(bairro);

    return pessoaDTOMock;
  }

  @Test
  public void listarTeste() {
    var pages = new PageImpl<>(asList(
      new Pessoa(),
      new Pessoa(),
      new Pessoa()
    ));

    when(pessoaRepository.findAll(any(), anyString(),
                                  anyString(), anyString(), anyString()
    )).thenReturn(pages);

    when(modelMapper.map(any(), eq(PessoaDTO.class))).thenReturn(new PessoaDTO());

    var pessoaPage = sut.listar(isA(Pageable.class), anyString(), anyString(), anyString(),
                                anyString()
    );

    verify(modelMapper, times(3)).map(any(), eq(PessoaDTO.class));
    verify(pessoaRepository, times(1)).findAll(
      any(), anyString(), anyString(), anyString(), anyString()
    );

    assertEquals(pessoaPage.getTotalElements(), pages.getTotalElements());
  }

  @Test
  public void salvarTeste() {
    when(modelMapper.map(any(), any())).thenAnswer((invocation -> {
      var source = invocation.getArgument(0);
      var classType = invocation.getArgument(1);
      if(source instanceof PessoaSaveDTO && classType.getClass().isInstance(Pessoa.class)) {
        return umaPessoa();
      }
      else if(source instanceof Pessoa && classType.getClass().isInstance(PessoaDTO.class)) {
        return umaPessoaDTO();
      }
      throw new InvalidUseOfMatchersException(
        String.format("Argument %s does not match", source));
    }));
    when(bairroRepository.findById(anyLong())).thenReturn(Optional.of(umBairro()));
    when(pessoaRepository.save(any(Pessoa.class))).thenReturn(umaPessoa());

    var pessoa = umaPessoaSaveDTO();

    var expected = sut.salvar(pessoa);

    verify(modelMapper, times(2)).map(any(), any());
    verify(bairroRepository, times(1)).findById(anyLong());
    verify(pessoaRepository, times(1)).save(any(Pessoa.class));

    assertEquals(pessoa.getNome(), expected.getNome(), "Deve possuir o mesmo nome");
    assertEquals(pessoa.getCpf(), expected.getCpf(), "Deve possuir o mesmo email");
    assertEquals(pessoa.getEmail(), expected.getEmail(), "Deve possuir o mesmo cpf");
  }

/*  @Test
  public void dataNascimentoValidaTeste() {
    when(modelMapper.map(any(), any())).thenAnswer((invocation -> {
      var source = invocation.getArgument(0);
      var classType = invocation.getArgument(1);
      if(source instanceof PessoaSaveDTO && classType.getClass().isInstance(Pessoa.class)) {
        var p = umaPessoa();
        p.setDataNascimento(LocalDate.of(2023, 1, 20));
        return p;
      }
      else if(source instanceof Pessoa && classType.getClass().isInstance(PessoaDTO.class)) {
        return umaPessoaDTO();
      }
      throw new InvalidUseOfMatchersException(
        String.format("Argument %s does not match", source));
    }));
    when(bairroRepository.findById(anyLong())).thenReturn(Optional.of(umBairro()));

    var pessoa = umaPessoaSaveDTO();
    pessoa.setDataNascimento("20/01/2023");

    var exception = assertThrows(NegocioException.class, () -> sut.salvar(pessoa));

    assertEquals("pessoa.dataNascimentoFutura", exception.getMessage());

    verify(modelMapper, times(1)).map(any(), any());
    verify(bairroRepository, times(1)).findById(anyLong());
    verify(pessoaRepository, never()).save(any(Pessoa.class));
  }
*/
  @Test
  public void buscarPorIdExcecaoTeste() {
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

    var exception = assertThrows(RegistroNaoEncontradoException.class, () -> sut.buscar(1L));

    assertEquals("pessoa.naoEncontrada", exception.getMessage());

    verify(modelMapper, never()).map(any(), eq(PessoaDTO.class));
    verify(pessoaRepository, times(1)).findById(anyLong());
  }

  @Test public void salvarPessoaBairroNuloTeste() {
    when(bairroRepository.findById(anyLong())).thenReturn(Optional.empty());
    when(modelMapper.map(any(), any())).thenReturn(umaPessoa());

    var exception = assertThrows(
      RegistroNaoEncontradoException.class,
      () -> sut.salvar(umaPessoaSaveDTO())
    );

    assertEquals("bairro.naoEncontrado", exception.getMessage());

    verify(bairroRepository, times(1)).findById(anyLong());
    verify(modelMapper, times(1)).map(any(), any());
  }

  @Test
  public void buscarPorIdTeste() {
    var mock = umaPessoaDTO();
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.of(umaPessoa()));
    when(modelMapper.map(any(), eq(PessoaDTO.class))).thenReturn(mock);

    var expected = sut.buscar(1L);

    verify(modelMapper, times(1)).map(any(), eq(PessoaDTO.class));
    verify(pessoaRepository, times(1)).findById(anyLong());

    assertEquals(expected.getId(), mock.getId(), "Deve possuir o mesmo id");
    assertEquals(expected.getNome(), mock.getNome(), "Deve possuir o mesmo nome");
    assertEquals(expected.getEmail(), mock.getEmail(), "Deve possuir o mesmo email");
  }

  @Test
  public void excluirTeste() {
    doNothing().when(pessoaRepository).deleteById(anyLong());

    sut.excluir(1L);

    verify(pessoaRepository, times(1)).deleteById(anyLong());
  }
}
