package com.elias_gill.poliplanner.controller;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.elias_gill.poliplanner.excel.ExcelService;
import com.elias_gill.poliplanner.security.TokenValidator;

@Controller
public class ExcelSyncController {
    @Autowired
    ExcelService service;

    @Autowired
    TokenValidator tokenValidator;

    private final static Logger logger = LoggerFactory.getLogger(ExcelSyncController.class);

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
     * @param file       El nuevo archivo subido de manera manual.
     * @return {@code 200 OK} si la sincronización fue exitosa,
     *         {@code 403 Forbidden} si el token es incorrecto o no está presente,
     *         {@code 500 Internal Server Error} si ocurre un error durante la
     *         sincronización.
     */
    @PostMapping("/sync")
    public ResponseEntity<?> syncExcel(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {

        logger.warn(">>> POST '/sync' alcanzado");

        try {
            tokenValidator.validate(authHeader);

            if (file.isEmpty()) {
                logger.warn("Archivo no recibido");
                return ResponseEntity.badRequest().body("No se recibió un archivo válido.");
            }

            // Guardar archivo temporal
            Path tempFile = Files.createTempFile("upload-", ".xlsx");
            file.transferTo(tempFile);

            logger.info("Archivo subido y guardado en {}", tempFile);

            // Procesar archivo como nuevo Excel
            service.persistSubjectsFromExcel(tempFile, tempFile.getFileName().toString());
            logger.warn("Excel correctamente parseado y actualizado");

            return ResponseEntity.ok("Archivo sincronizado correctamente.");
        } catch (Exception e) {
            logger.error("Error al sincronizar Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo sincronizar: " + e.getMessage());
        }
    }

    /*
     * Funciona exactamente igual que el endpoint "/sync", per esta pensado para
     * automatizacion con web scrapping.
     *
     * Este endpoint al recibir una request, tratara de scrapear la web de la
     * universidad en busca de nuevos horarios.
     */
    @PostMapping("/sync/ci")
    public ResponseEntity<?> syncExcel(@RequestHeader("Authorization") String authHeader) {
        logger.warn(">>> POST '/sync/ci' alcanzado desde CI/CD");
        try {
            tokenValidator.validate(authHeader);
            service.SyncronizeExcel();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("Error al sincronizar Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo sincronizar: " + e.getMessage());
        }
    }

    @GetMapping("/sync")
    public String showSyncForm() {
        logger.info("Accediendo a formulario de sincronizacion excel");
        return "pages/sync/form";
    }
}
