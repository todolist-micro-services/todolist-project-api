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
import todolist.database.dataType.Project;
import todolist.database.dataType.User;

@RestController
@RequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeleteProjectController {

    private final DataInterface dataInterface;

    public DeleteProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<Object> deleteProject(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String projectId
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        final User user = this.dataInterface.retrieveUserFromToken(authorization[1]);
        try {
            if (this.dataInterface.retrieveProjectById(Integer.parseInt(projectId)).creator.userId != user.userId)
                return ResponseEntity
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("{\"error\": \"user can only update it own projects\"}");
            if (!this.dataInterface.deleteProject(Integer.parseInt(projectId)).isEmpty())
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"Internal server error\"}");
            return new ResponseEntity<>("ok", HttpStatus.NO_CONTENT);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"project id must be a number\"}");
        }
    }

}
