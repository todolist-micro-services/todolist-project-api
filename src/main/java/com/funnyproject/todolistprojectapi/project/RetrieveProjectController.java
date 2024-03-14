package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.dto.ProjectDto;
import com.funnyproject.todolistprojectapi.dto.UserDto;
import com.funnyproject.todolistprojectapi.utils.InitDataInterface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Project;
import todolist.database.dataType.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/projects", produces = "application/json")
public class RetrieveProjectController {

    private final DataInterface dataInterface;

    public RetrieveProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @GetMapping("/user-project/{projectId}")
    public ResponseEntity<Object> retrieveOneUserProject(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String projectId
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        final User user = this.dataInterface.retrieveUserFromToken(authorization[1]);
        try {
            final Project project = this.dataInterface.retrieveOneUserProject(user.userId, Integer.parseInt(projectId));
            if (project == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND) .body("{\"error\": \"Project not found\"}");
            final ProjectDto projectDto = new ProjectDto(project.projectId, project.name, project.description,
                    project.creationDate, project.creator.userId);
            return new ResponseEntity<>(projectDto, HttpStatus.OK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"project id must be a number\"}");
        }
    }

    @GetMapping("/user-projects")
    public ResponseEntity<Object> retrieveAllUserProject(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        final String[] authorization = authorizationHeader.split(" ");
        List<ProjectDto> projectsDto = new ArrayList<>();

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        final User user = this.dataInterface.retrieveUserFromToken(authorization[1]);
        final List<Project> projects = this.dataInterface.retrieveAllUserProjects(user.userId);
        if (projects == null) {
            return new ResponseEntity<>(projectsDto, HttpStatus.OK);
        }
        for (int i = 0; i != projects.size(); ++i)
            projectsDto.add(new ProjectDto(projects.get(i).projectId, projects.get(i).name, projects.get(i).description,
                    projects.get(i).creationDate, projects.get(i).creator.userId));
        return new ResponseEntity<>(projectsDto, HttpStatus.OK);
    }

    @GetMapping("/project-users/{project}")
    public ResponseEntity<Object> retrieveProjectUsers(
            @PathVariable String project
    ) {
        try {
            List<UserDto> userDtos = new ArrayList<>();
            final List<User> users = this.dataInterface.retrieveAllUserLinkToProject(Integer.parseInt(project));
            if (users != null)
                for (int i = 0; i != users.size(); ++i)
                    userDtos.add(new UserDto((long) users.get(i).userId, users.get(i).firstname, users.get(i).lastname, users.get(i).email));
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"project id must be a number\"}");
        }
    }
}
