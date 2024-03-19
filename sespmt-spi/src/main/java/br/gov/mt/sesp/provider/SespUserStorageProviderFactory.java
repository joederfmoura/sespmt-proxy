package br.gov.mt.sesp.provider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.naming.InitialContext;

public class SespUserStorageProviderFactory implements UserStorageProviderFactory<SespUserStorageProvider> {

    @Override
    public SespUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        try {
            InitialContext ctx = new InitialContext();
            
            SespUserStorageProvider provider = (SespUserStorageProvider) ctx
                    .lookup("java:global/sespmt-spi/" + SespUserStorageProvider.class.getSimpleName());

            provider.setModel(model);
            provider.setSession(session);

            return provider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return "sespmt-spi";
    }
}
