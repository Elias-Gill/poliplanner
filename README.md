# Sistema de Armado de Horarios - Poliplanner

Este es un sistema web desarrollado en Java con Spring Boot que permite consultar y armar sus
horarios personales a partir de archivos oficiales de Excel publicados por la facultad.

La actualizacion de estos archivos se hace de forma automatica (ya no hay necesidad de
descargar los excel y subirlos a la app de manera manual).

<img width="1690" height="862" alt="poliplanner" src="https://github.com/user-attachments/assets/4613af93-1f0c-468d-94ad-45e3b8ee80df" />

## Levantar entorno de desarrollo

### Requisitos

- Java 17 o superior  
- Maven  
- PostgreSQL  
- Gnumeric (`ssconvert`) – requerido **solo** para correr los tests de integracion  
- Make (opcional, para comandos de conveniencia)

### Configuración

1. Clonar el repositorio  
2. Configurar la variables de entorno necesarias especificadas en `example.env` con tus valores locales
   (credenciales de base de datos, `UPDATE_KEY`, etc.)
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
Los tests de integracion se corren en una base de datos en memoria.

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
- `example.env`:
  Archivo de ejemplo para definir variables necesarias

## Cómo Colaborar

- Actualmente se aceptan PRs directo a la rama `master`, pero esto puede cambiar en el futuro  
- No hay un estándar estricto de código aún, pero se utiliza el formateador de Eclipse, disponible para su uso:
  `make format`.
- Se recomienda revisar y actualizar la documentación en `docs/` si se introducen cambios
  significativos

---

**NOTA**:
se encarece leer la documentación del proyecto contenida en [docs/index.md](docs/index.md)
