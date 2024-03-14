package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.utils.CheckRight;
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
        System.out.println("coucou 2");
        ResponseEntity<Object> response = this.checkParameters(linkUserToProjectRequest);
        System.out.println("coucou 3");
        if (response != null)
            return response;
        System.out.println("coucou 4");
        if (!CheckRight.isLinkToProject(Integer.parseInt(linkUserToProjectRequest.getUser()), Integer.parseInt(linkUserToProjectRequest.getProject()), dataInterface))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("{\"Error\": \"must be link to project\"}");
        System.out.println("coucou 1");
        System.out.println(Integer.parseInt(linkUserToProjectRequest.getUser()));
        System.out.println( Integer.parseInt(linkUserToProjectRequest.getProject()));
        System.out.println("coucou 2");
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
        if (!CheckRight.isLinkToProject(Integer.parseInt(linkUserToProjectRequest.getUser()), Integer.parseInt(linkUserToProjectRequest.getProject()), dataInterface))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("{\"Error\": \"must be link to project\"}");
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
