package com.elias_gill.poliplanner;

import com.elias_gill.poliplanner.controllers.ApiController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinMustache;
import static io.javalin.apibuilder.ApiBuilder.*;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinMustache());

            config.router.apiBuilder(() -> {
                path("/api", () -> {
                    get("/subjects", ApiController::getAvailableSubjects);
                    get("/login", ApiController::getAvailableSubjects);
                });
            });
        });

        app.start(8080);
    }
}
