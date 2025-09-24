package com.example.keycloakdemo.service;

import com.example.keycloakdemo.dto.RegisterRequest;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class KeycloakAdminService {
    @Value("${app.keycloak.server-url}")
    private String serverUrl;
    @Value("${app.keycloak.admin-realm}")
    private String adminRealm;
    @Value("${app.keycloak.admin-username}")
    private String adminUsername;
    @Value("${app.keycloak.admin-password}")
    private String adminPassword;
    @Value("${app.keycloak.target-realm}")
    private String targetRealm;

    private Keycloak kc;

    @PostConstruct
    void init() {
        this.kc = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    public void registerUser(RegisterRequest req) {
        RolesResource roles = kc.realm(targetRealm).roles();
        RoleRepresentation role = roles.get(req.role()).toRepresentation();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setEnabled(true);

        UsersResource users = kc.realm(targetRealm).users();
        Response resp = users.create(user);
        if (resp.getStatus() >= 300) {
            throw new IllegalStateException("Keycloak user create error: HTTP " + resp.getStatus());
        }
        String path = resp.getLocation().getPath();
        String userId = path.substring(path.lastIndexOf('/') + 1);

        CredentialRepresentation pwd = new CredentialRepresentation();
        pwd.setType(CredentialRepresentation.PASSWORD);
        pwd.setTemporary(false);
        pwd.setValue(req.password());
        users.get(userId).resetPassword(pwd);

        users.get(userId).roles().realmLevel().add(List.of(role));
    }
}
