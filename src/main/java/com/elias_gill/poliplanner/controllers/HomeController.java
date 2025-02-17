package com.elias_gill.poliplanner.controllers;

import io.javalin.http.Context;

public class HomeController {
    public static void handleHome(Context ctx) {
        ctx.render("templates/home.html");
    }
}
