package br.gov.mt.sesp.autenticacao.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String url;

    private String adminRealm;

    private String adminUser;

    private String adminPassword;

    private String realm;

    private String clientId;

    private String clientSecret;

    private String tokenContext;

    private String tokenAdminContext;

    private String addUserContext;

    private String userInfoContext;

    private String clientsContext;

    private String availableRolesContext;

    private String addRoleContext;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdminRealm() {
        return adminRealm;
    }

    public void setAdminRealm(String adminRealm) {
        this.adminRealm = adminRealm;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTokenContext() {
        return tokenContext;
    }

    public void setTokenContext(String tokenContext) {
        this.tokenContext = tokenContext;
    }

    public String getTokenAdminContext() {
        return tokenAdminContext;
    }

    public void setTokenAdminContext(String tokenAdminContext) {
        this.tokenAdminContext = tokenAdminContext;
    }

    public String getAddUserContext() {
        return addUserContext;
    }

    public void setAddUserContext(String addUserContext) {
        this.addUserContext = addUserContext;
    }

    public String getUserInfoContext() {
        return userInfoContext;
    }

    public void setUserInfoContext(String userInfoContext) {
        this.userInfoContext = userInfoContext;
    }

    public String getClientsContext() {
        return clientsContext;
    }

    public void setClientsContext(String clientsContext) {
        this.clientsContext = clientsContext;
    }

    public String getAvailableRolesContext() {
        return availableRolesContext;
    }

    public void setAvailableRolesContext(String availableRolesContext) {
        this.availableRolesContext = availableRolesContext;
    }

    public String getAddRoleContext() {
        return addRoleContext;
    }

    public void setAddRoleContext(String addRoleContext) {
        this.addRoleContext = addRoleContext;
    }

    public String getTokenURL() {
        return String.format(url + tokenContext, realm);
    }

    public String getTokenAdminURL() {
        return url + tokenAdminContext;
    }

    public String getUserInfoURL() {
        return String.format(url + userInfoContext, realm);
    }

    public String getAddUserURL() {
        return String.format(url + addUserContext, adminRealm);
    }

    public String getClientsURL() {
        return String.format(url + clientsContext, adminRealm, realm);
    }

    public String getAvailableRolesURL(String userId, String clientId) {
        return String.format(url + availableRolesContext, adminRealm, realm, userId, clientId);
    }

    public String getAddRoleURL(String userId, String clientId) {
        return String.format(url + addRoleContext, adminRealm, realm, userId, clientId);
    }
}
