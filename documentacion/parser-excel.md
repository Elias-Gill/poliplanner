# Proceso de Parseo del Archivo Excel

Este documento explica cómo se procesa el archivo Excel descargado de la facultad para extraer
las materias disponibles por carrera y cómo se guarda esta información en la base de datos.

## Flujo de Trabajo

1. **Estructura del archivo Excel**:
- El archivo Excel contiene varias hojas, cada una representando las materias disponibles para
  una carrera específica.
- Se ignoran las hojas de "Villarrica", "Cnel Oviedo" y "Códigos", ya que no representan
  carreras válidas dentro de la facultad y el formato de esas hojas es diferente.
- Cada hoja contiene columnas que incluyen, entre otras, el nombre de la materia, código,
  horarios, entre otros datos.

**Nota**:
Aunque el formato del archivo Excel debería ser consistente, la facultad podría modificar el
formato, lo que podría romper el parseo.
Sin embargo, en teoría, el formato es estable y no debería cambiar.

2. **Proceso de Parseo**:
- El archivo Excel se convierte a varios archivos CSV utilizando **Gnumeric** (`ssconvert`),
  que genera archivos CSV separados para cada carrera.
- Se ignoran archivos no relevantes como las hojas de "Villarrica" y "Cnel Oviedo", y se
  mantienen solo los archivos CSV correspondientes a las carreras de la facultad.
- Los archivos CSV se limpian eliminando encabezados y secciones no necesarias.
- Después de limpiar los archivos, estos se guardan temporalmente y se procesan uno por uno.

3. **Estructura de la Base de Datos**:
- Los datos procesados se organizan en una estructura de tablas que reflejan las carreras y
  materias disponibles.
  La base de datos tiene la siguiente estructura:
- **Subject**:
  Representa una materia.
- **Career**:
  Representa una carrera, que puede tener múltiples materias.
- **SheetVersion**:
  Guarda la versión del archivo Excel utilizado para el parseo.

**Modelo**:
```plaintext
+-------------+      +-------------+      +------------------+
|   Subject   |------|   Career    |------|   SheetVersion   |
+-------------+      +-------------+      +------------------+
|  subject_id |      | career_id   |      |   version_id     |
|  name       |      | subject_id  |      |   version        |
+-------------+      | name        |      |   parsedAt       |
                     +-------------+      +------------------+
```

4. **Mapeo y Persistencia**:
- El mapeo entre el archivo CSV y la base de datos se hace a través de un **DTO** llamado
  `SubjectCsv`, que mapea a un `Subject` en la base de datos.
- Aunque no se implementan validaciones para evitar datos duplicados, se asume que los datos en
  el Excel no se repiten.
- El proceso de inserción en la base de datos es transaccional.
  Si alguna inserción falla en cualquier parte del proceso, todo el proceso se cancela y
  revierte.

5. **Transformación de Datos**:
- En el proceso de parseo, se utiliza la clase `CustomDateSanitizer` para transformar las
  fechas a un formato válido.
  Esto se hace con Java, utilizando funciones personalizadas para asegurar que las fechas sean
  interpretadas correctamente.

6. **Errores y Logs**:
- Actualmente, no se implementan mecanismos de reintentos automáticos.
  Si ocurre un error durante el parseo o la inserción en la base de datos, se generaría un
  error en el endpoint que será capturado por GitHub Actions, y la acción puede notificar el
  fallo.
- No se ha implementado un sistema de logs aún, pero este debería registrarse en la base de
  datos en el futuro para el seguimiento de errores y notificaciones.

## Diagrama de flujo

```plaintext
+------------------------+
|   Convertir Excel a    |
|     Archivos CSV       |
+-----------+------------+
            |
            v
+-------------------------+
|   Filtrar Hojas Irrelev.|
|   (Villarrica, etc.)    |
+-----------+-------------+
            |
            v
+------------------------------+
| Limpiar Encabezados y Partes |
|      No Relevante            |
+-----------+------------------+
            |
            v
+----------------------------+
| Guardar Archivos Limpiados |
|    en Carpeta Temporal     |
+----------------------------+
            |
            v
+----------------------------+
|   Procesar CSV por Carrera |
|  (Línea a Línea: Materia)  |
+-----------+----------------+
            |
            v
+--------------------------+
| Mapeo e Inserción en Base|
|        de Datos          |
+-----------+--------------+
            |
            v
+--------------------------+
|    Commit Transaccional  |
|     Si Todo es Exitoso   |
+--------------------------+
```

### Consideraciones adicionales

- **Manejo de Errores**:
  El proceso de parseo no cuenta con un sistema de reintentos, pero cualquier fallo en el
  proceso es capturado y puede generar un error que GitHub Actions puede notificar.
- **Dependencias**:
  Utilizamos **Gnumeric** (ssconvert) para convertir los archivos Excel en CSV, y **OpenCV** en
  la limpieza de los datos, asegurando que los archivos sean válidos antes de procesarlos.
- **Integridad de Datos**:
  La inserción en la base de datos se realiza dentro de una transacción para garantizar que si
  algo falla, el sistema pueda revertir todo el proceso sin afectar la base de datos.
