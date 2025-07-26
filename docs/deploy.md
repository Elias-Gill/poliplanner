# Guia de Deploy

## Dependencias

- Maven
- Java 17
- Postgresql 17
- Gnumeric (comando ssconvert)
- [API KEY](google_drive.md) de Google Console

NOTA:
de momento solo funciona en servers Linux, dado que se depende de Gnumeric, solo disponible
para distribuciones Linux.

La descarga de las dependencias de Gnumeric no son necesarias, dado que para el funcionamiento
del comando `ssconvert` solo se requiere del paquete principal, por lo que basta con:

```bash
apt install --no-install-recommends gnumeric
```

## Configuracion del entorno

Se deben configurar las siguientes variables de entorno:
* `UPDATE_KEY`:
  Password que se utiliza para proteger el acceso a los endpoints `/sync` y `/sync/ci`,
  encargados de la actualización de planillas Excel.
  No corresponde a ninguna API externa.
* `DB_URL`:
  URL de conexión a la base de datos PostgreSQL en formato JDBC.
* `LOG_PATH`:
  Ruta donde se guardan los logs de la aplicación.
* `GOOGLE_API_KEY`:
  Clave de API para acceder a carpetas y archivos publicos de Google Drive.

NOTA:
ejemplo de url jdbc:
`jdbc:postgresql://localhost:5432/poliplanner?user=postgres&password=postgres`

## Startup del server

Se puede inicilizar el server directamente con `mvn spring-boot:run` o ejecutando el jar
compilado. 

Los binarios exportados por el comando `mvn package` son autocontenidos y distribuibles.
