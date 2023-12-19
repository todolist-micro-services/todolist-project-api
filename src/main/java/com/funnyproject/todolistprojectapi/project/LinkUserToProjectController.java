package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;

@RestController
@RequestMapping(value = "/projects", produces = "application/json")
public class LinkUserToProjectController {

    private final DataInterface dataInterface;

    public LinkUserToProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @PostMapping("/link")
    public ResponseEntity<Object> addLinkBetweenUserAndProject(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody LinkUserToProjectRequest linkUserToProjectRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(linkUserToProjectRequest);
        if (response != null)
            return response;
        final String dbResponse = this.dataInterface.linkUserToProject(Integer.parseInt(linkUserToProjectRequest.getUser()), Integer.parseInt(linkUserToProjectRequest.getProject()));
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"Error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("{\"Link\": \"Successful\"}");
    }

    @DeleteMapping("/link")
    public ResponseEntity<Object> deleteLinkBetweenUserAndProject(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody LinkUserToProjectRequest linkUserToProjectRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(linkUserToProjectRequest);
        if (response != null)
            return response;
        final String dbResponse = this.dataInterface.unLinkUserToProject(Integer.parseInt(linkUserToProjectRequest.getUser()), Integer.parseInt(linkUserToProjectRequest.getProject()));
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"Error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("{\"Unlink\": \"Successful\"}");
    }

    private ResponseEntity<Object> checkParameters(LinkUserToProjectRequest linkUserToProjectRequest) {
        try {
            validateProjectCreationRequest(linkUserToProjectRequest);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"Missing parameters, needs : user and project\"}");
        }
        return null;
    }

    private void validateProjectCreationRequest(LinkUserToProjectRequest linkUserToProjectRequest) {
        if (linkUserToProjectRequest == null ||
                linkUserToProjectRequest.getProject() == null ||
                linkUserToProjectRequest.getUser() == null)
            throw new IllegalArgumentException("Missing required parameters");
    }
}
