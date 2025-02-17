package com.elias_gill.poliplanner.controllers;

import com.elias_gill.poliplanner.parser.Parser;

import io.javalin.http.Context;

public class ApiController {
    public static void getAvailableSubjects(Context ctx) {
        ctx.json(Parser.parseFile());
    }
}
