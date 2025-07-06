package com.elias_gill.poliplanner.controller;

import com.elias_gill.poliplanner.excel.ExcelService;
import com.elias_gill.poliplanner.security.TokenValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class ExcelSyncController {
    @Autowired
    ExcelService service;

    @Autowired
    TokenValidator tokenValidator;

    /**
     * Sincroniza el archivo Excel más reciente disponible desde la web de origen.
     *
     * <p>
     * Este endpoint está protegido mediante autenticación con un token tipo Bearer
     * pasado en el header `Authorization`. El valor esperado debe coincidir con la
     * variable de entorno {@code UPDATE_KEY}.
     *
     * <p>
     * Al recibir una solicitud válida, descarga el nuevo Excel, lo convierte a CSV,
     * lo parsea, y actualiza la base de datos con la nueva información.
     *
     * @param authHeader Header HTTP de autorización con el token Bearer.
     * @return {@code 200 OK} si la sincronización fue exitosa,
     *         {@code 403 Forbidden} si el token es incorrecto o no está presente,
     *         {@code 500 Internal Server Error} si ocurre un error durante la
     *         sincronización.
     */
    @PostMapping("/sync")
    public ResponseEntity<?> syncExcel(@RequestHeader("Authorization") String authHeader) {
        try {
            tokenValidator.validate(authHeader);
            service.SyncronizeExcel();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo sincronizar: " + e);
        }
    }

    @GetMapping("/sync")
    public String showSyncForm() {
        return "pages/sync";
    }
}
