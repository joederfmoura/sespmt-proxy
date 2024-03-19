import {version} from '../../package.json';

const url_version = 'v1-0';

export const environment = {
  production: false,
  version: version,
  api_registro_geral: `/${url_version}/registro-geral`,
  api_carteira_funcional: `/${url_version}/carteira-funcional`,
  api_gerenciamento_ocorrencia: `/${url_version}/gerenciamento-ocorrencia`,
  api_gerenciamento_patrulha: `/${url_version}/gerenciamento-patrulha`,
  api_gerenciamento_procurado: `/${url_version}/gerenciamento-procurado`,
  api_autenticacao: `/${url_version}/autenticacao`
}

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
