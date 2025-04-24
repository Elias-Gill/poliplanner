# Sistema de Armado de Horarios - Poliplanner

Este es un sistema web desarrollado en Java con Spring Boot que permite consultar y armar sus
horarios personales a partir de archivos oficiales de Excel publicados por la facultad.

La actualizacion de estos archivos se hace de forma automatica (ya no hay necesidad de
descargar los excel y subirlos a la app de manera manual).

## Levantar el Proyecto

### Requisitos

- Java 17 o superior  
- Maven  
- PostgreSQL  
- Gnumeric (`ssconvert`) – requerido **solo** para correr los tests  
- Make (opcional, para comandos de conveniencia)

### Configuración

1. Clonar el repositorio  
2. Copiar el archivo `.env.example` a `.env` y configurarlo con tus valores locales
   (credenciales de base de datos, `UPDATEKEY`, etc.)  
3. Levantar la base de datos PostgreSQL si aún no lo hiciste  
4. Correr el proyecto:
   
```bash
make seed # cargar los datos semilla
make run # correr el proyecto
```
o 
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--seed
mvn spring-boot:run
```

## Testing

Este proyecto cuenta con pruebas **unitarias** y **de integración**.

- Tests unitarios (requieren Gnumeric instalado):

```bash
make test
```

- Tests de integración:

```bash
make test-integration
```

- Ambos:

```bash
make test-full
```

También podés correrlos con Maven:

```bash
mvn test -Dgroups=unit
mvn test -Dgroups=integration
mvn test -Dgroups=unit,integration
```

## Estructura del Proyecto

- `docs/`:
  Documentación detallada de la arquitectura, componentes y decisiones de diseño.
  Iniciar por [docs/index.md](docs/index.md)  
- `src/`:
  Código fuente de la aplicación  
- `Makefile`:
  Comandos útiles para desarrollo  
- `.env.example`:
  Archivo de ejemplo para definir variables necesarias

## Cómo Colaborar

- Actualmente se aceptan PRs directo a la rama `master`, pero esto puede cambiar en el futuro  
- No hay un estándar estricto de código aún, pero se utiliza el formateador por defecto del LSP
  de Eclipse  
- Se recomienda revisar y actualizar la documentación en `docs/` si se introducen cambios
  significativos

## Seguridad y Producción

- Las credenciales y claves sensibles se definen mediante variables de entorno  
- El sistema puede actualizar automáticamente los datos al llamar a un endpoint protegido con
  una contraseña (`UPDATEKEY`), usado en conjunción con GitHub Actions para tareas programadas

---

**NOTA**:
se encarece leer la documentación del proyecto contenida en [docs/index.md](docs/index.md)
