# Arquitectura general del sistema

Este documento describe la arquitectura general de la aplicación web para armado de horarios
universitarios.

## Visión general

La aplicación permite a los usuarios:
- Registrarse e iniciar sesión
- Consultar materias disponibles
- Crear uno o más horarios personales
- Calcular su promedio de examenes
- Consultar guias educativas

No existen roles diferenciados:
todos los usuarios tienen las mismas capacidades.

El frontend está compuesto por HTML y CSS con plantillas de Thymeleaf.
Para mas informacion sobre los estilos, consultar la guia de [estilos](estilos_css.md).

## Componentes y modulos

La aplicacion se divide en modulos siguiendo las practicas de desarrollo de Spring Boot y java
con modularizacion mediante Maven:

- service
    - Servicios sobre los modelos
- web
    - Controllers
    - Plantillas thymeleaf
- excel
    - Parser
    - Web scrapper
- persistence
    - Modelos 
    - Repositorios
    - Migraciones
- app
    - Punto de entrada de la aplicacion
    - Devtools
    - Test de integracion

## Flujo principal de uso

1. Usuario se registra e inicia sesión (cookies y sesiones estándar de Spring)
2. Se presenta una pantalla para elegir la versión de horario (Excel parseado) 3.
   Luego de seleccionar el Excel, el usuario puede ver las materias 4.
   El usuario puede crear y guardar uno o más horarios en base a esa versión 5.
   Los horarios se almacenan en la base de datos y son personales

## Infraestructura

- **Backend + Frontend** están contenidos en una única aplicación Spring Boot
- La base de datos usada es **PostgreSQL**
- Los archivos Excel se descargan y se **parsean inmediatamente**, almacenando su contenido en
  la base de datos
- El archivo original excel **no se conserva**

## Consideraciones

- No se aplican validaciones sobre solapamiento de materias, ya que es común que los alumnos
  lleven materias superpuestas
- Las guias y la calculadora son accesibles sin inicio de sesión.
