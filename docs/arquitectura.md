# Arquitectura general del sistema

Este documento describe la arquitectura general de la aplicación web para armado de horarios
universitarios.

# Indice de documentacion

- `[Arquitectura general](arquitectura.md)`:
- `[sincronizacion-excel.md](sincronizacion-excel.md)`:
  descarga, scraping y parseo
- `[parser-excel.md](parser-excel.md)`:
  cómo se transforma un Excel en estructuras persistentes
- `horarios.md`:
  lógica de armado de horarios y estructura de datos
- `usuarios.md`:
  modelo de usuario, autenticación y sesiones

## Visión general

La aplicación permite a los usuarios:
- Registrarse e iniciar sesión
- Elegir una versión de horario (proveniente de un archivo Excel parseado)
- Consultar materias disponibles
- Crear uno o más horarios personales basados en dicha versión

No existen roles diferenciados:
todos los usuarios tienen las mismas capacidades.

El frontend está compuesto por HTML y CSS con plantillas de Spring Boot y algo de **htmx** para
una experiencia más fluida sin utilizar JavaScript frontend moderno.

## Componentes principales

```ascii
+-------------------------------+
|         Usuario final         |
+-------------------------------+
        |
        v
+-------------------------------+
|         Controlador web       |
|  (Login, elección Excel, UI) |
+-------------------------------+
        |
        v
+-------------------------------+
|         Backend Spring        |
| - Parser de Excel             |
| - Armador de horarios         |
| - Servicio de autenticación   |
+-------------------------------+
        |
        v
+-------------------------------+
|         Base de datos         |
| (Usuarios, Horarios, Materias |
|  Versiones Excel parseadas)   |
+-------------------------------+
```

## Flujo principal de uso

1. Usuario se registra e inicia sesión (cookies y sesiones estándar de Spring)
2. Se presenta una pantalla para elegir la versión de horario (Excel parseado) 3.
   Luego de seleccionar el Excel, el usuario puede ver las materias 4.
   El usuario puede crear y guardar uno o más horarios en base a esa versión 5.
   Los horarios se almacenan en la base de datos y son personales

## Infraestructura

- **Backend + Frontend** están contenidos en una única aplicación Spring Boot
- Se despliega en **Fly.io** como un solo contenedor
- La base de datos usada es **PostgreSQL** (remota)
- Los archivos Excel se descargan y se **parsean inmediatamente**, almacenando su contenido en
  la base
- El archivo original **no se conserva**

## Consideraciones

- No se aplican validaciones sobre solapamiento de materias, ya que es común que los alumnos
  lleven materias superpuestas
- Se considera más adelante agregar advertencias visuales en el frontend para solapamientos
