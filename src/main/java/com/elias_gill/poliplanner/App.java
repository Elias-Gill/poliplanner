package com.elias_gill.poliplanner;

import java.util.HashMap;

import com.elias_gill.poliplanner.parser.*;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinMustache;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinMustache());
        });

        var map = new HashMap<String, String>();
        map.put("saludo", "Hola a todos desde thymeleaf");

        app.get("/", ctx -> ctx.result(Parser.parseFile().toString()))
                .get("/home", ctx -> ctx.render("templates/home.html", map))
                .start(7070);
    }
}
