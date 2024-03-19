package br.gov.mt.sesp.carteirafuncional;

import br.gov.mt.sesp.carteirafuncional.controllers.AdministrativoController;
import br.gov.mt.sesp.carteirafuncional.dtos.AdministrativoSaveDTO;
import br.gov.mt.sesp.carteirafuncional.services.AdministrativoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "RH")
@SpringBootTest
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class AdministrativoControllerTeste {

  private MockMvc mvc;

  @Autowired
  private AdministrativoController administrativoController;
  @Autowired
  private AdministrativoService administrativoService;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  public void setUp() {
    this.mvc = MockMvcBuilders.standaloneSetup(administrativoController)
                 .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                 .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                 .build();
  }

  @Test
  void contextLoads() {
  }


  @Test
  public void testPOSTController() throws Exception {
    var administrativo = new AdministrativoSaveDTO();
    administrativo.setNome("Maria de Souza");
    administrativo.setNomeMae("Letícia de Souza Campos");
    administrativo.setMatricula("123456");
    administrativo.setTelefones("(65) 3661-6161");
    administrativo.setEmail("maria@email.com");
    administrativo.setCpf("045.331.321-97");
    administrativo.setRua("Rua 78");
    administrativo.setNumero("316");
    administrativo.setComplemento("Apto. 310");
    administrativo.setCep("78895-310");
    administrativo.setIdBairro(1L);
    administrativo.setIdSetor(1L);
    administrativo.setDataNascimento("08/06/1976");

    this.mvc.perform(
      MockMvcRequestBuilders.post("/administrativos").contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(administrativo)))
      .andExpect(status().isCreated());
  }

  @Test
  public void testGETbyIdController() throws Exception {
    var administrativo = new AdministrativoSaveDTO();
    administrativo.setNome("Maria de Souza");
    administrativo.setNomeMae("Letícia de Souza Campos");
    administrativo.setDataNascimento("08/06/1976");
    administrativo.setMatricula("123456");
    administrativo.setTelefones("(65) 3661-6161");
    administrativo.setEmail("maria@email.com");
    administrativo.setCpf("045.331.321-97");
    administrativo.setRua("Rua 78");
    administrativo.setNumero("316");
    administrativo.setComplemento("Apto. 310");
    administrativo.setCep("78895-310");
    administrativo.setIdBairro(1L);
    administrativo.setIdSetor(1L);

    var administrativoSaved = administrativoService.salvar(administrativo);

    this.mvc
      .perform(MockMvcRequestBuilders.get("/administrativos/" + administrativoSaved.getId()))
      .andExpect(status().isOk());
  }

  @Test
  public void testGETAllController() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders.get("/administrativos"))
      .andExpect(status().isOk());
  }

  @Test
  public void testPUTController() throws Exception {
    var administrativo = new AdministrativoSaveDTO();
    administrativo.setNome("Maria de Souza");
    administrativo.setNomeMae("Letícia de Souza Campos");
    administrativo.setMatricula("123456");
    administrativo.setTelefones("(65) 3661-6161");
    administrativo.setEmail("maria@email.com");
    administrativo.setCpf("045.331.321-97");
    administrativo.setRua("Rua 78");
    administrativo.setNumero("316");
    administrativo.setComplemento("Apto. 310");
    administrativo.setCep("78895-310");
    administrativo.setIdBairro(1L);
    administrativo.setIdSetor(1L);
    administrativo.setDataNascimento("08/06/1976");

    var administrativoId = administrativoService.salvar(administrativo).getId();

    administrativo.setMatricula("321456");
    administrativo.setCep("78895-313");

    this.mvc.perform(
      MockMvcRequestBuilders
        .put("/administrativos/" + administrativoId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(administrativo)))
      .andExpect(status().isOk());
  }


}
