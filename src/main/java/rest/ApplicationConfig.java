package rest;

import Controllers.ISecurityController;
import Controllers.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dtos.UserDTO;
import exceptions.ApiException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import persistence.HibernateConfig;

import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationConfig {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
    private ISecurityController securityController = new SecurityController(emf);
    private ObjectMapper om = new ObjectMapper();
    private static ApplicationConfig instance;
    private Javalin app;

    private ApplicationConfig() {
        ObjectMapper om = new ObjectMapper();
        app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            config.routing.contextPath = "/api";
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.allowedOrigins().add("https://cphbusinessprojekt.dk");
                });
            });
        });
    }

    public static ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    public ApplicationConfig initiateServer() {
        return instance;
    }

    public ApplicationConfig startServer(int portNumber) {
        app.start(portNumber);
        return instance;
    }

    public ApplicationConfig setRoute(EndpointGroup route) {
        app.routes(route);
        return instance;
    }

    public ApplicationConfig setExceptionHandlers() {
        app.exception(Exception.class, (e, ctx) -> {
            ObjectNode node = om.createObjectNode().put("errorMessage", e.getMessage());
            ctx.status(500).json(node);
        });

        app.error(404, ctx -> {
            ctx.status(404).result("Not Found");
        });

        app.exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(400).result("Bad Request: " + e.getMessage());
        });
        return instance;
    }

    public ApplicationConfig checkSecurityRoles() {
        app.updateConfig(config -> {
            config.accessManager((handler, ctx, permittedRoles) -> {
                Set<String> allowedRoles = permittedRoles.stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
                if (allowedRoles.contains("ANYONE") || ctx.method().toString().equals("OPTIONS")) {
                    handler.handle(ctx);
                    return;
                }

                UserDTO user = ctx.attribute("user");
                System.out.println("USER IN CHECK_SEC_ROLES: " + user);
                if (user == null)
                    ctx.status(HttpStatus.FORBIDDEN)
                            .json(om.createObjectNode()
                                    .put("msg", "Not authorized. No username were added from the token"));

                if (securityController.authorize(user, allowedRoles))
                    handler.handle(ctx);
                else
                    throw new ApiException(HttpStatus.FORBIDDEN.getCode(), "Unauthorized with roles: " + allowedRoles);
            });
        });
        return instance;
    }

    public void stopServer() {
        app.stop();
    }
}