package br.gov.mt.sesp.carteirafuncional;

import br.gov.mt.sesp.carteirafuncional.controllers.PolicialController;
import br.gov.mt.sesp.carteirafuncional.dtos.PolicialSaveDTO;
import br.gov.mt.sesp.carteirafuncional.services.PolicialService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "RH")
@SpringBootTest
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class PolicialControllerTeste {

  private MockMvc mvc;

  @Autowired
  private PolicialController policialController;
  @Autowired
  private PolicialService policialService;

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
    this.mvc =
      MockMvcBuilders.standaloneSetup(policialController)
        .setCustomArgumentResolvers(
          new PageableHandlerMethodArgumentResolver())
        .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView()).build();
  }

  @Test
  void contextLoads() {
  }


  @Test
  public void testPOSTController() throws Exception {
    var policial = new PolicialSaveDTO();
    policial.setNome("João de Soares");
    policial.setMatricula("123456");
    policial.setDataNascimento("08/06/1976");
    policial.setNomeMae("Letícia Soares Campos");
    policial.setTelefones("(65) 3661-6161");
    policial.setEmail("joao@email.com");
    policial.setCpf("012.111.321-90");
    policial.setRua("Rua 1");
    policial.setNumero("456");
    policial.setComplemento("Apto. 213");
    policial.setCep("78085-310");
    policial.setIdBairro(1L);
    policial.setIdBatalhao(1L);

    this.mvc.perform(
      MockMvcRequestBuilders.post("/policiais")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(policial)))
      .andExpect(status().isCreated());
  }

  @Test
  public void testGETbyIdController() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders.get("/policiais/" + 1)).andExpect(status().isOk());
  }

  @Test
  public void testGETAllController() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders.get("/policiais")).andExpect(status().isOk());
  }

  @Test
  public void testPUTController() throws Exception {
    var policial = new PolicialSaveDTO();
    policial.setNome("João de Soares");
    policial.setNomeMae("Letícia Soares Campos");
    policial.setDataNascimento("08/06/1976");
    policial.setMatricula("123456");
    policial.setTelefones("(65) 3661-6161");
    policial.setEmail("joao@email.com");
    policial.setCpf("012.111.321-90");
    policial.setRua("Rua 1");
    policial.setNumero("456");
    policial.setComplemento("Apto. 213");
    policial.setCep("78085-310");
    policial.setIdBairro(1L);
    policial.setIdBatalhao(1L);

    var administrativoId = policialService.salvar(policial).getId();

    policial.setMatricula("321456");
    policial.setCep("78895-313");

    this.mvc.perform(
      MockMvcRequestBuilders
        .put("/policiais/" + administrativoId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(policial)))
      .andExpect(status().isOk());
  }

}
