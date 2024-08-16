package com.example.jarvis.controller;
import com.example.jarvis.entity.ResponseToken;
import com.example.jarvis.entity.User;
import com.example.jarvis.serviceIml.KeycloakServiceImpl;
import com.example.jarvis.serviceIml.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/user")
public class Controller {
    private final String postUser= """
                               {
                                  "username": "Somu",
                                  "password": "string",
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "phone": "+917217899394"
                               }
            """;
    private final String putUser= """
                               {
                                  "id": "111",
                                  "kid":"212",
                                  "username": "Somu",
                                  "password": "string",
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "phone": "+917217899394"
                               }
            """;
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private KeycloakServiceImpl keycloakService;

    @Operation(tags = "Create User API",
            description = "This api is used to create user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User creation payload",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = postUser)))
    )
    @PostMapping("/create")
    public String create(@RequestBody User user)
    {

        return keycloakService.createUser(user);
//        return userService.createUser(user);
    }

    @Operation(tags = "Get Single User API",
            description = "This api is giving single user"
//            parameters = {
//            @Parameter(
//                    name = "Custom Parameter",
//                    description = "Custom header parameter",
//                    required = false,
//                    in = ParameterIn.HEADER
//            )}
    )
    @GetMapping("/get-user")
    public User getUser(@RequestParam Long id)
    {
        return userService.getSingleUser(id);
    }


    @Operation(tags = "Get All User API",
            description = "This api is used to get all users"
    )
    @GetMapping("/get-all-users")
    public List<User> getALlUsers()
    {
        return userService.getALlusers();
    }
    @Operation(tags = "Update User API",
            description = "This api is used to update user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                       content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = putUser)))
    )
    @PutMapping("/update")
    public String update(@RequestBody  User user)
    {
        return keycloakService.updateUser(user);
    }

    @Operation(tags = "Delete User API",
            description = "This api is used to delete user"
    )
    @DeleteMapping("/delete")
    public String delete(@RequestParam Long id)
    {
        return keycloakService.deleteUserFromKeycloakAndDatabase(id);
    }

    @Operation(tags = "Login User API",
            description = "This api is used to login user",
            responses = {
                    @ApiResponse(responseCode = "200",description = "OK"),
                    @ApiResponse(responseCode = "401",description = "UnAuthorized")
            }
    )
    @PostMapping("/login")
    public ResponseToken login(@RequestBody User user)
    {
        return keycloakService.login(user);
    }
}
