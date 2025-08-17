# Guia de Deploy

## Dependencias

- Maven
- Java 17
- Postgresql 17
- [API KEY](google_drive.md) de Google Console

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

Los binarios exportados por el comando `mvn package` son autocontenidos y distribuibles.

Para iniciar el server en el perfil de produccion:

```bash
java -jar <compiled_file>.jar -noverify --spring.profiles.active=prod
```

Tambien se cuenta con un Dockerfile que puede ser ejecutado.

## Despliegue

El despliegue se realiza en la plataforma de `fly.io` y la base de datos se encuentra
actualmente alojada en `neon.com`.

El deploy automatico se realiza mediante github actions, activadas cuando se hace un push de
tags.
Las tags tienen formato de version estandar `vX.X.X-patch`

El proyecto se piensa para no depender de ningun proveedor especifico, y asegurando que la
migracion sea lo mas rapida y facil posible.

--- 

Informacion sobre la arquitectura del proyecto [aqui](arquitectura.md).
