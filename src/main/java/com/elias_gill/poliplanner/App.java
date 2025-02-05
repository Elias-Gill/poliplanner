package com.elias_gill.poliplanner;

import com.elias_gill.poliplanner.parser.*;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinThymeleaf());
        });

        app.get("/", ctx -> ctx.result(Parser.parseFile().toString()))
                .get("/home", ctx -> ctx.render("templates/home.html"))
                .start(7070);
    }
}
