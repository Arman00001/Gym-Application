package com.epam.gymapp;

import com.epam.gymapp.configuration.AppConfig;
import com.epam.gymapp.configuration.OpenApiConfiguration;
import com.epam.gymapp.configuration.RepoConfig;
import com.epam.gymapp.configuration.WebConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class GymServerApplication {

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();

        tomcat.setPort(8080);
        tomcat.getConnector();

        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext("", docBase);

        AnnotationConfigWebApplicationContext applicationContext =
                new AnnotationConfigWebApplicationContext();

        applicationContext.register(
                AppConfig.class,
                RepoConfig.class,
                WebConfig.class,
                OpenApiConfiguration.class
        );

        DispatcherServlet dispatcherServlet =
                new DispatcherServlet(applicationContext);

        Tomcat.addServlet(context, "dispatcher", dispatcherServlet);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();

        System.out.println("Gym app started:");
        System.out.println("http://localhost:8080");

        tomcat.getServer().await();
    }
}