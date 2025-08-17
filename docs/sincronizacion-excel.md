# Sincronización Automática del Excel

Este sistema permite mantener actualizada la base de datos con la información de los horarios
de la facultad.
Una vez al dia se realiza un scraping de la página web de la facultad para obtener la versión
más reciente del archivo Excel, que luego se parsea y se guarda en la base de datos.

## Flujo de Trabajo

1. **Scraping del archivo Excel más reciente**: 
    - Se realiza un scraping de la página web de la facultad para encontrar el archivo Excel
      más reciente.
    - Los sitios de descarga de los distintos archivos excel son listados para la extraccion de
      la version mas reciente.
    - El nombre del archivo se usa para determinar la versión más reciente.
      Ejemplo:
      **`Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx`**.
      Aquí, la fecha **19122024** se usa para identificar la versión.
    - Los archivos pueden alojarse en carpetas publicas de Google Drive, en ese caso se listan
      todos los links pertenecientes a Drive y se extraen los archivos y fuentes.
      Para mas informacion sobre este proceso consultar esta [guia](google_drive.md).
2. **Descarga del archivo Excel**: 
    - Una vez encontrado el archivo más reciente, se descarga el archivo a través de una
      solicitud HTTP en el backend de la aplicación.
    - Si se descarga una nueva versión, los datos de la base de datos se actualizan con la
      nueva información, pero los archivos Excel no se conservan.
3. **Parseo e inserción de datos en la base de datos**:
    - El archivo Excel descargado se parsea inmediatamente, y la información contenida se
      guarda directamente en las tablas de la base de datos.
    - Los archivos Excel como tal no se almacenan; solo se guarda la información parseada.
    - Para mas detalles sobre el proceso de parseo consultar esta [guia](parser-excel.md).
4. **Protección y reversión en caso de error**:
    - La actualización de la base de datos se realiza dentro de una transacción.
      Si en algún punto ocurre un error (ya sea en la descarga o en el parseo), se realiza una
      reversión para mantener la integridad de los datos.
5. **Automatización**:
    - La actualización automática se programa utilizando **GitHub Actions** con un cron job
      configurado para ejecutarse una vez al dia.
    - Un endpoint (`sync/ci`) protegido con contraseña (almacenada en la variable de entorno
      `UPDATE_KEY`) es llamado para disparar el proceso de sincronización.
    - Tambien se cuenta con un endpoint para la subida manual de archivo (`sync/`).
