package com.microservice.systemservice.utils;

import com.microservice.systemservice.services.ProjectsService;
import org.springframework.stereotype.Component;

@Component
public class UserBean {

    private static ProjectsService projectsService;

    private static volatile String userInfo = new String();

    public UserBean(ProjectsService projectsService) {
        UserBean.projectsService = projectsService;
    }
    public static void init(){
        //String qwe = "123";
        userInfo = "user info details string test";
    }

    public static String getUserInfo(){
        return userInfo;
    }
}
