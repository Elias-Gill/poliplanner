package com.elias_gill.poliplanner;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import org.hibernate.Session;

import com.elias_gill.poliplanner.controllers.ApiController;
import com.elias_gill.poliplanner.database.AppHibernate;
import com.elias_gill.poliplanner.models.User;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinMustache;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinMustache());

            config.router.apiBuilder(() -> {
                path("/api", () -> {
                    get("/subjects", ApiController::getAvailableSubjects);
                    get("/users", ctx -> {
                        // Retrieve a user by ID
                        try (Session session = AppHibernate.getSessionFactory().openSession()) {
                            User user = session.get(User.class, 1L); // Fetch user with ID 1
                            System.out.println("Retrieved user: " + user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            AppHibernate.shutdown(); // Close the SessionFactory
                        }
                    });
                    get("/login", ApiController::getAvailableSubjects);
                });
            });
        });

        app.start(8080);
    }
}
