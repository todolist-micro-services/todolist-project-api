package com.funnyproject.todolistprojectapi.project;

import com.funnyproject.todolistprojectapi.AppConfig;
import com.funnyproject.todolistprojectapi.utils.InitDataInterface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import todolist.database.DataInterface;

@RestController
@RequestMapping("/users")
public class DeleteProjectController {

    private final DataInterface dataInterface;

    public DeleteProjectController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, HttpServletRequest request) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        if (!this.dataInterface.deleteUser(authorization[1]).isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
