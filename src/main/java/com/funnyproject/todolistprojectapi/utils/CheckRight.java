package com.funnyproject.todolistprojectapi.utils;

import todolist.database.DataInterface;
import todolist.database.dataType.User;

import java.util.List;

public class CheckRight {
    public static boolean isLinkToProject(int userId, int projectId, DataInterface dataInterface) {
        List<User> users = dataInterface.retrieveAllUserLinkToProject(projectId);
        System.out.println("1");
        System.out.println(users.size());
        System.out.println("2");

        for (int  i = 0; i != users.size(); ++i)
            if (users.get(i).userId == userId)
                return true;
        return false;
    }
}
