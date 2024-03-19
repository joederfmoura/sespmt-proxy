package br.gov.mt.sesp.provider;

import br.gov.mt.sesp.model.Role;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import br.gov.mt.sesp.adapter.UserAdapter;
import br.gov.mt.sesp.model.User;
import br.gov.mt.sesp.repository.UsuarioRepository;
import br.gov.mt.sesp.util.ShaPasswordEncoder;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Stateful
@Local(SespUserStorageProvider.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class SespUserStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider,
        CredentialInputUpdater, CredentialInputValidator {

    private static final String CLIENT_ID = "gerenciamento-roubo";

    private KeycloakSession session;
    private ComponentModel model;
    private UsuarioRepository usuarioRepository;

    @PersistenceContext
    private EntityManager em;

    public void setModel(ComponentModel model) {
        this.model = model;
    }

    public void setSession(KeycloakSession session) {
        this.session = session;
    }

    private UsuarioRepository getUsuarioRepository() {
        if (usuarioRepository == null) {
            usuarioRepository = new UsuarioRepository(em);
        }

        return usuarioRepository;
    }

    private boolean isInvalidCredential(CredentialInput input) {
        boolean credentialTypeNotSupported = !supportsCredentialType(input.getType());
        boolean invalidCredentialModel = !(input instanceof UserCredentialModel);

        return credentialTypeNotSupported || invalidCredentialModel;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (isInvalidCredential(input)) {
            return false;
        }

        UserCredentialModel userCredentialModel = (UserCredentialModel) input;
        User usuario = getUsuarioRepository().findByUsername(user.getUsername());

        if (usuario == null) {
            return false;
        }

        return new ShaPasswordEncoder().matches(userCredentialModel.getChallengeResponse(), usuario.getPassword());
    }

    @Transactional
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (isInvalidCredential(input)) {
            return false;
        }

        UserCredentialModel userCredentialModel = (UserCredentialModel) input;

        String encodedPassword = new ShaPasswordEncoder().encode(userCredentialModel.getChallengeResponse());

        return getUsuarioRepository().updatePassword(user.getUsername(), encodedPassword);
    }

    @Override
    public void disableCredentialType(RealmModel realmModel, UserModel userModel, String s) {
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realmModel, UserModel userModel) {
        return Collections.emptySet();
    }

    @Remove
    @Override
    public void close() {

    }

    private UserAdapter getUserAdapterWithRoles(RealmModel realm, User user) {
        UserAdapter userAdapter = new UserAdapter(session, realm, model, user);

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                RoleModel roleModel = realm.getClientByClientId(CLIENT_ID).getRole(role.getName());

                if (!userAdapter.getRoleMappings().contains(roleModel)) {
                    userAdapter.grantRole(roleModel);
                }
            }
        }

        return userAdapter;
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        try {
            Long externalId = Long.parseLong(StorageId.externalId(id));
            User user = getUsuarioRepository().find(externalId);

            return getUserAdapterWithRoles(realm, user);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        User user = getUsuarioRepository().findByUsername(username);

        if (user == null) {
            return null;
        }

        return getUserAdapterWithRoles(realm, user);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
    	User user = getUsuarioRepository().findByEmail(email);
    	if(user == null) {
    		return null;
    	}

        return getUserAdapterWithRoles(realm, user);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return getUsuarioRepository().count();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return getUsuarioRepository()
                .findAll()
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResultss) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return getUsuarioRepository()
                .findAll(search)
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return getUsuarioRepository()
                .findAll(search, firstResult, maxResults)
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return getUsuarioRepository()
                .findAll()
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        return getUsuarioRepository()
                .findAll(firstResult, maxResults)
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return getUsuarioRepository()
                .findAll(firstResult, maxResults)
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return getUsuarioRepository()
                .findAll()
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return getUsuarioRepository()
                .findAll()
                .stream()
                .map(user -> getUserAdapterWithRoles(realm, user))
                .collect(Collectors.toList());
    }
}
