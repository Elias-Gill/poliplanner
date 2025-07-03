# Guía de desarrollo

Esta guía explica cómo preparar el entorno de desarrollo, levantar el proyecto, ejecutar los
tests y mantener el código formateado de manera consistente.

## Requisitos

- Java 17
- Maven
- PostgreSQL con una base de datos llamada `poliplanner`
- Configurar las variables de entorno especificadas en el archivo `env_example`

No se utiliza Docker ni servicios externos.
Toda la ejecución es local.

## Levantar el proyecto

Para iniciar la aplicación:

```bash
make run
````

Esto ejecuta `mvn spring-boot:run`.

## Carga de datos semilla

Para precargar datos útiles durante el desarrollo:

```bash
make seed
```

Para limpiar y recargar los datos:

```bash
make clean-seed
```

## Testing

El proyecto utiliza JUnit junto con Spring Boot Test Starter.
Hay tres categorías de pruebas agrupadas:

```bash
make test            # Pruebas unitarias
make test-integration
make test-full       # Unitarias + de integración
```

Los tests se ejecutan sobre una base en memoria (H2).

## Formato y estilo de código

Se utiliza Spotless como herramienta de formateo.

Aplicar formato:

```bash
make format
```

Verificar formato:

```bash
make lint
```

## Estructura del proyecto

La organización del código está explicada en los siguientes documentos:

* `arquitectura.md`:
  estructura general del sistema
* `sincronizacion-excel.md`:
  scraping y descarga de archivos
* `parser-excel.md`:
  procesamiento y persistencia de datos desde Excel
* `horarios.md`:
  lógica de armado de horarios
* `usuarios.md`:
  modelo de usuario y autenticación

## Flujo de trabajo

Actualmente no hay una convención de ramas definida.
Todo el trabajo debe realizarse como pull requests hacia la rama `master`.
Correr el formatter antes de hacer PRs.

## Despliegue

No hay entorno de producción configurado ni proceso de despliegue definido.
El proyecto se encuentra en etapa de desarrollo.
