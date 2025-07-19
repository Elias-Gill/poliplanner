# Sincronización Automática del Excel

Este sistema permite mantener actualizada la base de datos con la información de los horarios
de la facultad.
Cada 6 horas, se realiza un scraping de la página web de la facultad para obtener la versión
más reciente del archivo Excel, que luego se parsea y se guarda en la base de datos.

## Flujo de Trabajo

1. **Scraping del archivo Excel más reciente**: 
- Utilizando Java puro, se realiza un scraping de la página web de la facultad para encontrar
  el archivo Excel más reciente.
- El nombre del archivo Excel sigue la convención **`Horario *** date_of_update.xlsx`**.
  Esto permite identificar el archivo más reciente comparando las fechas de actualización.
- Los archivos pueden alojarse en carpetas publicas de Google Drive, en ese caso se listan
  todos los links pertenecientes a Drive y se extraen los archivos y fuentes.
  Para mas informacion sobre este proceso consultar esta [guia](google_drive.md).

2. **Descarga del archivo Excel**: 
- Una vez encontrado el archivo más reciente, se descarga el archivo a través de una solicitud
  HTTP en el backend de la aplicación.

3. **Parseo e inserción de datos en la base de datos**:
- El archivo Excel descargado se parsea inmediatamente, y la información contenida se guarda
  directamente en las tablas de la base de datos.
- Los archivos Excel como tal no se almacenan; solo se guarda la información parseada.
- Para mas detalles sobre el proceso de parseo consultar esta [guia](parser-excel.md).

4. **Versión de archivos Excel**:
- El nombre del archivo se usa para determinar la versión más reciente.
  Ejemplo:
  **`Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx`**.
  Aquí, la fecha **19122024** se usa para identificar la versión.
- Si se descarga una nueva versión, los datos de la base de datos se actualizan con la nueva
  información, pero los archivos Excel no se conservan.

5. **Protección y reversión en caso de error**:
- La actualización de la base de datos se realiza dentro de una transacción.
  Si en algún punto ocurre un error (ya sea en la descarga o en el parseo), se realiza una
  reversión para mantener la integridad de los datos.

6. **Automatización**:
- La actualización automática se programa utilizando **GitHub Actions** con un cron job
  configurado para ejecutarse cada 10 horas.
- Un endpoint protegido con contraseña (almacenada en la variable de entorno `UPDATE_KEY`) es
  llamado para disparar el proceso de sincronización.

## Diagramas de flujo

```plaintext
+------------------------+        +-----------------------+
|   Scraping del Excel   |        |      Descargar Excel  |
|      (Java puro)       | -----> |    (Backend Spring)   |
+------------------------+        +-----------------------+
        |                             |
        v                             v
+-------------------------+     +----------------------------+
|     Parseo del Excel    | --> |    Inserción en Base de    |
|  (Java + base de datos) |     |      Datos (Transacciones) |
+-------------------------+     +----------------------------+
        |
        v
+--------------------------------+
|   Verificación de Versiones    |
|  (Comparar fechas en nombre)   |
+--------------------------------+
        |
        v
+-------------------------------+
|   Actualización Automática    |
|  (GitHub Actions + Endpoint)  |
+-------------------------------+
```

## Detalles técnicos

- **Scraping**:
  Realizado con Java puro (sin dependencias externas), el scraping utiliza la URL de la página
  de la facultad para localizar el archivo Excel más reciente basándose en la fecha del nombre
  del archivo.
- **Base de Datos**:
  La información parseada se guarda en una base de datos PostgreSQL.
  El archivo Excel original no se conserva.
- **Automatización**:
  El proceso de sincronización se ejecuta cada 6 horas mediante un cron job de GitHub Actions.
  Esto asegura que los datos estén siempre actualizados sin intervención manual.

### Consideraciones adicionales

- **Errores y logs**:
  Aunque aún no se ha implementado un sistema de logs, se plantea registrar los errores en la
  base de datos, lo cual permitirá hacer un seguimiento detallado de los posibles problemas que
  surjan durante el proceso de sincronización.
- **Transacciones y reversión**:
  Si algo falla en el proceso (descarga, parseo, inserción), el sistema revertirá la
  transacción para garantizar que los datos en la base de datos se mantengan consistentes.
