package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Project;
import todolist.database.dataType.User;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class UpdateProjectController {

    private final DataInterface dataInterface;

    public UpdateProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateProject(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateProjectRequest updateProjectRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        User user = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (this.dataInterface.retrieveProjectById(Integer.parseInt(updateProjectRequest.getId())).creator.userId != user.userId)
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"user can only update it own created projects\"}");
        ResponseEntity<Object> response = this.checkParameters(updateProjectRequest);
        if (response != null)
            return response;
        final Project project = new Project(Integer.parseInt(updateProjectRequest.getId()),
                updateProjectRequest.getName(), updateProjectRequest.getDescription(),
                LocalDateTime.parse(updateProjectRequest.getCreationDate().replace(" ", "T")),
                new User(Integer.parseInt(updateProjectRequest.getCreator()),
                        "", "", "", ""));
        return getProjectCreatedId(project, updateProjectRequest.getCreator(), updateProjectRequest.getName());
    }

    private ResponseEntity<Object> getProjectCreatedId(Project project, String creator, String name) {
        final String createProjectResponse = this.dataInterface.updateProject(project);
        if (!createProjectResponse.isEmpty())
            return new ResponseEntity<>(createProjectResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        final Project projectCreated = this.dataInterface
                .retrieveUserProjectByName(Integer.parseInt(creator), name);
        return new ResponseEntity<>(projectCreated.projectId, HttpStatus.CREATED);
    }

    private ResponseEntity<Object> checkParameters(UpdateProjectRequest updateProjectRequest) {
        try {
            validateProjectCreationRequest(updateProjectRequest);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"Missing parameters, needs : id, name, description, creationDate, creator\"}");
        }
        return null;
    }

    private void validateProjectCreationRequest(UpdateProjectRequest updateProjectRequest) {
        if (updateProjectRequest == null ||
                updateProjectRequest.getCreator() == null ||
                updateProjectRequest.getCreationDate() == null ||
                updateProjectRequest.getDescription() == null ||
                updateProjectRequest.getName() == null ||
                updateProjectRequest.getId() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }
    }
}
