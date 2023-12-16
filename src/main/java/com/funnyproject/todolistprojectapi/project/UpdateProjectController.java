package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.dto.ProjectDto;
import com.funnyproject.todolistprojectapi.utils.InitDataInterface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.User;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UpdateProjectController {

    private final DataInterface dataInterface;

    public UpdateProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            HttpServletRequest request,
            @RequestBody UpdateProjectRequest updateUserRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");
        ResponseEntity<Object> checkBodyError = this.checkBody(updateUserRequest);
        User databaseUser;

        if (checkBodyError != null)
            return checkBodyError;
        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        if (!this.dataInterface.updateUser(authorization[1], new User(0, updateUserRequest.getFirstname(), updateUserRequest.getLastname(), databaseUser.email, databaseUser.password)).isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        return this.returnNewUser(authorization[1]);
    }

    private ResponseEntity<Object> returnNewUser(final String token) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private ResponseEntity<Object> checkBody(UpdateProjectRequest updateUserRequest) {
        try {
            this.validateUpdateRequest(updateUserRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"error\": \"Missing parameters, needs : firstname, lastname\"}");
        }
        return null;
    }

    private void validateUpdateRequest(UpdateProjectRequest updateUserRequest) {
        if (updateUserRequest == null || updateUserRequest.getFirstname() == null || updateUserRequest.getLastname() == null)
            throw new IllegalArgumentException("Missing required parameters");
    }

}
