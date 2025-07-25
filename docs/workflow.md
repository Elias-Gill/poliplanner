# Guía para el Entorno de Desarrollo

Esta guía explica cómo preparar el entorno de desarrollo, levantar el proyecto, ejecutar los
tests y mantener el código formateado de manera consistente.

**Nota**:
En este documento se hace referencia a comandos definidos en el archivo `Makefile`.
Estos y otros comandos pueden consultarse en dicho archivo, así como ejecutarse manualmente
desde la terminal.

La filosofía del proyecto es que todo comando, utilidad o procedimiento debe poder ejecutarse
fácilmente de forma manual desde una terminal `UNIX` (Linux o MacOs).
El soporte para entornos Windows y la ejecución mediante IDE son considerados secundarios (se
aceptan PRs para mejorar dicha experiencia).

## Requisitos para el desarrollo

- Java 17
- Maven
- PostgreSQL con una base de datos creada
- Configurar las variables de entorno especificadas en el archivo `example.env`
- (Recomendado) GNU Make

No se utiliza Docker ni servicios externos.
Toda la ejecución es local.

NOTA:
el URL de conexion de PostgreSQL local en formato jdbc es:
`jdbc:postgresql://localhost:5432/<nombre-de-la-bd>`

## Carga de Datos Semilla

Este comando crea un usuario por defecto (user=`pruebas`, password=`123`) y carga algunas
materias.

```bash
make seed
```

Para limpiar completamente la base de datos y recargar los datos:

```bash
make clean-seed
```

## Levantar el Proyecto

Para iniciar la aplicación:

```bash
make run
```

**Nota**:
Otra filosofía del proyecto es que el entorno de desarrollo debe poder ejecutarse sin depender
de API keys de servicios de terceros.
Pueden mostrarse advertencias, pero siempre debe permitirse el desarrollo normal.
Los servicios externos deben poder simularse, desactivarse o degradarse de manera controlada
para no bloquear el trabajo local.

## Testing

El proyecto utiliza JUnit junto con Spring Boot Test Starter.
Hay tres categorías de pruebas agrupadas:

```bash
make test               # Pruebas unitarias
make test-integration   # Pruebas de integracion (VER REQUISITOS)
make test-full          # Unitarias + de integración
```

Los tests se ejecutan sobre una base de datos "in-memmory" (H2).

### Requerimientos para los Tests de Integración

Para la correcta ejecución de los tests de integración se deben proporcionar las siguientes
dependencias:

* El comando `ssconvert`, parte de la aplicación
  [Gnumeric](https://es.wikipedia.org/wiki/Gnumeric).
* API key para Google Drive.
  Para más información, consultar esta [guía](google_drive.md).

**Nota**:
Si estos requerimientos no se cumplen, los tests simplemente deben ser salteados, dejando un
aviso (`warning`) indicando que no se pudieron ejecutar correctamente y que faltan ciertas
dependencias.

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

## Flujo de Trabajo y Ética de Desarrollo

### Ramas y Pull Requests

Actualmente todo el trabajo debe realizarse mediante **Pull Requests (PRs)** dirigidos hacia la
rama principal `master`.
No está permitido realizar *commits* directos a `master`.

### Proceso para Pull Requests

1. Todo PR debe ser revisado antes de ser fusionado.
2. Los PRs deben mantenerse **pequeños, claros y enfocados**.
   Evita enviar muchos cambios no relacionados en un solo PR.
3. Es preferible enviar varios PRs pequeños y bien explicados que uno grande y difícil de
   revisar.
4. Todo cambio debe respetar las **guías de estilo y formato de código** del proyecto.
   Usa los comandos disponibles en `Makefile` para asegurarlo antes de subir tu código.
5. Toda nueva funcionalidad debe incluir sus respectivos **tests**, o al menos una
   justificación clara en el PR de por qué no aplica.
6. Si el código afecta flujos críticos, se debe explicar claramente en la descripción del PR
   cómo probarlo.
7. Si es posible, **referencia issues** o tickets relevantes.

### Ética de Desarrollo

- **Claridad y Transparencia**:
  Documenta lo que haces, ya sea en commits, en descripciones de PRs o en issues.
  Además, si tu cambio afecta la forma en que otras personas deben entender, usar o desarrollar
  el proyecto, ajusta, modifica o crea la documentación necesaria dentro del directorio
  `docs/`.
* **Comunicación abierta:** Consulta antes de tomar decisiones estructurales importantes.
  Prefiere preguntar antes que rehacer trabajo innecesario.
* **Consistencia:** Sigue las convenciones del proyecto, aunque prefieras otro estilo personal.
  Si crees que algo debe cambiar, propónlo con argumentos y consenso.
* **Respeto al Tiempo de los demás:** Escribe código claro, deja comentarios donde sea
  necesario y evita crear deuda técnica por prisa o descuido.

## Despliegue

El despliegue se realiza en la plataforma de `fly.io`.
De momento no existe convencion de tags, versionado o procesos de deploy automatico.

El proyecto se piensa para no depender de ningun proveedor especifico, y asegurando que la
migracion sea lo mas rapida y facil posible.
