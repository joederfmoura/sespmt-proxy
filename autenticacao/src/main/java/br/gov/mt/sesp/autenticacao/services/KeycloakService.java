package br.gov.mt.sesp.autenticacao.services;

import br.gov.mt.sesp.autenticacao.utils.HTTPUtil;
import br.gov.mt.sesp.autenticacao.utils.JWTUtil;
import br.gov.mt.sesp.autenticacao.utils.KeycloakProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KeycloakService {

    private KeycloakProperties keycloakProperties;

    @Autowired
    public KeycloakService(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    public String login(String usuario, String senha) throws IOException {
        String parametros = montarParametrosLogin(usuario, senha);

        return HTTPUtil.enviarRequisicao(keycloakProperties.getTokenURL(),
                HTTPUtil.HTTPMethod.POST,
                HTTPUtil.ContentType.FORM,
                parametros,
                null);
    }

    public String refresh(String refreshToken) throws IOException {
        String parametros = montarParametrosRefresh(refreshToken);

        return HTTPUtil.enviarRequisicao(keycloakProperties.getTokenURL(),
                HTTPUtil.HTTPMethod.POST,
                HTTPUtil.ContentType.FORM,
                parametros,
                null);
    }

    public String userInfo(String token) throws IOException {
        return HTTPUtil.enviarRequisicao(keycloakProperties.getUserInfoURL(),
                HTTPUtil.HTTPMethod.GET,
                HTTPUtil.ContentType.FORM,
                null,
                token);
    }

    public String loginGoogle(String token) throws JSONException, IOException {
        JSONObject payload = JWTUtil.getPayload(token);
        String resposta = null;

        try {
            String tokenAdmin = getTokenAdmin();
            addUser(token, tokenAdmin);
            String userId = getUserId(token);
            String clientId = getClientId(tokenAdmin);
            String roleId = getRoleId(userId, clientId, tokenAdmin);
            addRole(roleId, userId, clientId, tokenAdmin);
        } catch (Exception e) {
            System.out.println("Usuario ja cadastrado");
        }

        return login(payload.getString("email"), payload.getString("sub"));
    }

    private String montarParametrosLogin(String usuario, String senha) {
        return "client_id=" + keycloakProperties.getClientId() +
                "&client_secret=" + keycloakProperties.getClientSecret() +
                "&grant_type=password" +
                "&username=" + usuario +
                "&password=" + senha;
    }

    private String montarParametrosRefresh(String refreshToken) {
        return "client_id=" + keycloakProperties.getClientId() +
                "&client_secret=" + keycloakProperties.getClientSecret() +
                "&grant_type=refresh_token" +
                "&refresh_token=" + refreshToken;
    }

    private String getTokenAdmin() throws IOException, JSONException {
        JSONObject resposta = new JSONObject(HTTPUtil.enviarRequisicao(keycloakProperties.getTokenAdminURL(),
                HTTPUtil.HTTPMethod.POST,
                HTTPUtil.ContentType.FORM,
                montarParametrosTokenAdmin(),
                null));

        return (String) resposta.get("access_token");
    }

    private String montarParametrosTokenAdmin() {
        return "client_id=admin-cli" +
                "&grant_type=password" +
                "&username=" + keycloakProperties.getAdminUser() +
                "&password=" + keycloakProperties.getAdminPassword();
    }

    private void addUser(String token, String tokenAdmin) throws JSONException, IOException {
        String parametros = montarCorpoAddUser(JWTUtil.getPayload(token));

        HTTPUtil.enviarRequisicao(keycloakProperties.getAddUserURL(),
                HTTPUtil.HTTPMethod.POST,
                HTTPUtil.ContentType.JSON,
                parametros,
                tokenAdmin);
    }

    private String montarCorpoAddUser(JSONObject payload) throws JSONException {
        JSONObject dadosUsuario = new JSONObject();

        dadosUsuario.put("firstName", payload.getString("given_name"));
        dadosUsuario.put("lastName", payload.getString("family_name"));
        dadosUsuario.put("email", payload.getString("email"));
        dadosUsuario.put("username", payload.getString("email"));
        dadosUsuario.put("enabled", true);

        JSONArray dadosCredentials = new JSONArray();
        JSONObject dadosCredential = new JSONObject();

        dadosCredential.put("type", "password");
        dadosCredential.put("value", payload.get("sub"));
        dadosCredential.put("temporary", false);

        dadosCredentials.put(dadosCredential);
        dadosUsuario.put("credentials", dadosCredentials);

        return dadosUsuario.toString();
    }

    private String getClientId(String tokenAdmin) throws IOException, JSONException {
        String resposta = HTTPUtil.enviarRequisicao(keycloakProperties.getClientsURL(),
                HTTPUtil.HTTPMethod.GET,
                HTTPUtil.ContentType.FORM,
                null,
                tokenAdmin);

        JSONArray clients = new JSONArray(resposta);

        String idClient = null;
        int contador = 0;
        while (contador < clients.length()) {
            JSONObject client = new JSONObject(clients.get(contador).toString());

            if (client.get("clientId").equals(keycloakProperties.getClientId())) {
                idClient = (String) client.get("id");
                break;
            }
            contador++;
        }

        return idClient;
    }

    private String getRoleId(String userId, String clientId, String tokenAdmin) throws IOException, JSONException {
        String resposta = HTTPUtil.enviarRequisicao(keycloakProperties.getAvailableRolesURL(userId, clientId),
                HTTPUtil.HTTPMethod.GET,
                HTTPUtil.ContentType.FORM,
                null,
                tokenAdmin);

        JSONArray roles = new JSONArray(resposta);

        String idRole = null;
        int contador = 0;
        while (contador < roles.length()) {
            JSONObject role = new JSONObject(roles.get(contador).toString());

            if (role.get("name").equals("CD")) {
                idRole = role.getString("id");
                break;
            }
            contador++;
        }

        return idRole;
    }

    private String getUserId(String token) throws JSONException, IOException {
        JSONObject payload = JWTUtil.getPayload(token);

        String usuario = payload.getString("email");
        String senha = payload.getString("sub");

        String resposta = login(usuario, senha);
        String accesToken = new JSONObject(resposta).getString("access_token");

        JSONObject payloadResposta = JWTUtil.getPayload(accesToken);

        return payloadResposta.getString("sub");
    }

    private void addRole(String roleId, String userId, String clientId, String tokenAdmin) throws JSONException, IOException {
        JSONObject role = new JSONObject();
        role.put("id", roleId);
        role.put("name", "CD");

        JSONArray roles = new JSONArray();
        roles.put(role);

        String parametros = roles.toString();

        HTTPUtil.enviarRequisicao(keycloakProperties.getAddRoleURL(userId, clientId),
                HTTPUtil.HTTPMethod.POST,
                HTTPUtil.ContentType.JSON,
                parametros,
                tokenAdmin);
    }
}
