package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.dto.ProjectDto;
import com.funnyproject.todolistprojectapi.utils.InitDataInterface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.User;

@RestController
@RequestMapping("/users")
public class RetrieveProjectController {

    private final DataInterface dataInterface;

    public RetrieveProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @GetMapping("/me")
    public ResponseEntity<Object> retrieveUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, HttpServletRequest request) {
        ProjectDto user = new ProjectDto();
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.getUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        user.setEmail(databaseUser.email);
        user.setFirstname(databaseUser.firstname);
        user.setLastname(databaseUser.lastname);
        user.setId(databaseUser.userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
