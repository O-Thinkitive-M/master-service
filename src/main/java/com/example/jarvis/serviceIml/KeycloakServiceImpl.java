package com.example.jarvis.serviceIml;

import com.example.jarvis.entity.ResponseToken;
import com.example.jarvis.entity.User;
import com.example.jarvis.repo.UserRepo;
import com.example.jarvis.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class KeycloakServiceImpl {

    @Value("${keycloak.auth-server-url}")
    public String serverURL;
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.resource}")
    public String clientID;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    private String currentToken;

    public String createUser(User request) {
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(request.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEmailVerified(false);
        user.setEnabled(true);
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setCredentials(Collections.singletonList(credentialRepresentation));
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
//        System.out.println(usersResource.count());

        Response response;
        try {
            response = usersResource.create(user);
            log.info("INFO: " + response.getStatus());
            if(response.getStatus()==409)
            {
                return "User already exists!";
            }
            if (response.getStatus() == 201) {
                request.setKid(CreatedResponseUtil.getCreatedId(response));
                userService.addUser(request);
                log.info("New User Created");
                return "User Created Successfully!";

            }
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return "Error: "+ e.getMessage();
        }
        return "Something went wrong!";

    }
    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
    public ResponseToken login(User user) {
        ResponseToken token = getAccessToken(user);
        return token;
    }
    //    get Access Token
    public ResponseToken getAccessToken(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", "password");
        mapForm.add("username", user.getUsername());
        mapForm.add("password", user.getPassword());
        mapForm.add("client_id", clientID);
        mapForm.add("client_secret", clientSecret);
        mapForm.add("scope", "openid");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);
        ResponseEntity<ResponseToken> response = null;
        String serverUrl = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";

        try {
            response = restTemplate.exchange(serverUrl, HttpMethod.POST, request, ResponseToken.class);
            if (response != null && response.getBody() != null) {
                String accessToken = response.getBody().getAccessToken();
            }
        } catch (HttpClientErrorException httpClientErrorException) {
            log.info("Invalid Credentials");
        } catch (Exception exception) {
            log.info("Login Failed");
        }

        if (response == null) {
            return null;
        } else {
            ResponseToken userToken = response.getBody();
//            setCurrentToken(response.getBody().getAccessToken());
            String token = getUserIdFromUserInfoEndpoint(response.getBody().getAccessToken());
            currentToken = token;
            return userToken;
        }
    }

    private String getUserIdFromUserInfoEndpoint(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String userInfoUrl = serverURL + "/realms/" + realm + "/protocol/openid-connect/userinfo";
        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {
        });
        if (userInfoResponse != null && userInfoResponse.getBody() != null) {
            Map<String, Object> userInfo = userInfoResponse.getBody();
            return (String) userInfo.get("sub");
        } else {
            return null;
        }
    }
    public String getCurrentUsername() {
        return currentToken;
    }


    public String updateUser(User updateUser) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            User user1 = userService.getSingleUser(updateUser.getId());
            String kid = user1.getKid();
            UserRepresentation userRepresentation = usersResource.get(kid).toRepresentation();
            userRepresentation.setUsername(updateUser.getUsername());
            userRepresentation.setEmail(updateUser.getEmail());
            userRepresentation.setFirstName(updateUser.getFirstname());
            userRepresentation.setLastName(updateUser.getLastname());
            usersResource.get(kid).update(userRepresentation);
            log.info("User updated successfully");
            userRepo.save(updateUser);
            return "User Updated Successfully!";
        } catch (Exception e) {
            log.error("Error occurred while updating user details for user : {}", e.getMessage());
            return "Something went wrong!";
        }
    }

    public String deleteUserFromKeycloakAndDatabase(Long id)
    {
        try {
            User user=userRepo.findById(id).get();
            if(user!=null)
            {
                RealmResource realmResource = keycloak.realm(realm);
                realmResource.users().delete(user.getKid());
                userRepo.deleteById(id);
                log.info("User Delete Successfully!");
                return "User Delete Successfully!";
            }
        }
        catch (NoSuchElementException e)
        {
            e.printStackTrace();
            return "No Such User Present!";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error("Exception Problem!");
            return "Exception Problem!";
        }
        return "Developers Problem!";
    }
}