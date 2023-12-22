package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Project;
import todolist.database.dataType.User;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/projects", produces = "application/json")
public class CreateProjectController {

    private final DataInterface dataInterface;

    public CreateProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createProject(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CreateProjectRequest createProjectRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(createProjectRequest);
        if (response != null)
            return response;
        if (this.dataInterface.retrieveUserProjectByName(Integer.parseInt(createProjectRequest.getCreator()),
                createProjectRequest.getName()) != null)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Project with this name already exists\"}");
        final Project project = new Project(0, createProjectRequest.getName(),
                createProjectRequest.getDescription(),
                LocalDateTime.parse(createProjectRequest.getCreationDate().replace(" ", "T")),
                new User(Integer.parseInt(createProjectRequest.getCreator()),
                        "", "", "", ""));
        return getProjectCreatedId(project, createProjectRequest.getCreator(), createProjectRequest.getName());
    }

    private ResponseEntity<Object> getProjectCreatedId(Project project, String creator, String name) {
        final String createProjectResponse = this.dataInterface.createProject(project);
        if (!createProjectResponse.isEmpty())
            return new ResponseEntity<>(createProjectResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        final Project projectCreated = this.dataInterface
                .retrieveUserProjectByName(Integer.parseInt(creator), name);
        final String formatOutput = String.format("{\"project\": \"%s\"}", String.valueOf(projectCreated.projectId));
        return ResponseEntity.status(HttpStatus.CREATED).body(formatOutput);
    }

    private ResponseEntity<Object> checkParameters(CreateProjectRequest createProjectRequest) {
        try {
            validateProjectCreationRequest(createProjectRequest);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"Missing parameters, needs : name, description, creationDate, creator\"}");
        }
        return null;
    }

    private void validateProjectCreationRequest(CreateProjectRequest createProjectRequest) {
        if (createProjectRequest == null ||
                createProjectRequest.getCreator() == null ||
                createProjectRequest.getCreationDate() == null ||
                createProjectRequest.getDescription() == null ||
                createProjectRequest.getName() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }
    }
}
