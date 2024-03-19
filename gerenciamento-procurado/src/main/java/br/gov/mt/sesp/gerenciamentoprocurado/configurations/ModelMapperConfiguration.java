package br.gov.mt.sesp.gerenciamentoprocurado.configurations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração referente ao ModelMapper. Utilizado para conversão (de/para) 
 * dos modelos para DTO's e vice-versa.
 */
@Configuration
public class ModelMapperConfiguration {

  DateTimeFormatter diaMesAnoFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  /** 
   * Bean a ser injetado pelo Spring Boot ao solicitar uma injeção de dependência de ModelMapper.
   * 
   * @return ModelMapper instância de {@link ModelMapper} com configuração padrão
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    modelMapper.addConverter(getStringToLocalDateConverter());
    modelMapper.addConverter(getLocalDateToStringConverter());

    return modelMapper;
  }
  
  /** 
   * "Ensina" o ModelMapper a converter objetos do tipo {@link String} para {@link LocalDate}.
   * 
   * @return AbstractConverter<String, LocalDate> conversor configurado
   */
  private AbstractConverter<String, LocalDate> getStringToLocalDateConverter() {
    return new AbstractConverter<String, LocalDate>() {

      @Override
      protected LocalDate convert(String source) {
        if (source == null) {
          return null;
        }

        return LocalDate.parse(source, diaMesAnoFormatter);
      }
    };
  }
  
  /** 
   * "Ensina" o ModelMapper a converter objetos do tipo {@link LocalDate} para {@link String}.
   * 
   * @return AbstractConverter<LocalDate, String> conversor configurado
   */
  private AbstractConverter<LocalDate, String> getLocalDateToStringConverter() {
    return new AbstractConverter<LocalDate, String>() {

      @Override
      protected String convert(LocalDate source) {
        if (source == null) {
          return null;
        }

        return diaMesAnoFormatter.format(source);
      }
    };
  }
}
