# Proceso de Parseo del Archivo Excel

Este documento explica cómo se procesa el archivo Excel descargado de la facultad para extraer
las materias disponibles por carrera y cómo se guarda esta información en la base de datos.

## Flujo de Trabajo

1. **Estructura del archivo Excel**:
- El archivo Excel contiene varias hojas, cada una representando las materias disponibles para
  una carrera específica.
- El nombre de hoja determina el `Codigo de carrera`.
- Cada hoja contiene columnas que incluyen, entre otras, el nombre de la materia, código,
  horarios, entre otros datos.

**Nota**:
Aunque el formato del archivo Excel debería ser consistente, la facultad podría modificar el
formato, lo que podría romper el parseo.
Sin embargo, en teoría, el formato es estable y no debería cambiar.

### Formatos distintos o inconsistente

Por ejemplo, las hoja utilizadas tanto para Villarrica como Cnel.Oviedo son diferentes a las
utilizadas en la central de San Lorenzo.
Mas especificamente, las columnas que estan presentes difieren (a lo que llamaremos `layout` de
ahora en adelante).

Para solventar este problema (y ademas preveer posibles cambios en el excel), lo que se tiene
es una carpeta la cual cuenta con distintos archivos json, los cuales representan un "layout"
diferente (es decir, una combinacion de columnas diferente).

el formato de un layout json es el siguiente:
```json
{
    "lista": [
        { "encabezado": "item", "patron": ["item", "ítem"] },
        { "encabezado": "departamento", "patron": ["dpto", "dpto.", "departamento"] },
        { "encabezado": "asignatura", "patron": ["asignatura", "materia", "curso"] },
        { "encabezado": "nivel", "patron": ["nivel", "correlativas"] },
        { "encabezado": "semestre", "patron": ["sem/grupo", "semestre", "grupo"] },
        ...
}
```

Notar de que este layout debe ser exacto a lo presentado en la fila de encabezados del excel,
asi que el orden es sumamente importante.

Un `encabezado` es el atributo al cual hace referencia dentro del archivo excel.
Estos encabezados so iguales para todos los layouts, pero la presencia o ausencia de alguno de
los encabezados difiere entre layouts.
Para saber cuales son los encabezados se puede leer el codigo perteneciente al modulo `excel`,
mas especificamente el parser.

El `patron` representa un string para tratar de encontrar el encabezado, esto porque se puede
dar el caso de que un año para otro se agregue, por ejemplo, un acento, lo cual romperia el
buscador de patrones, pero el layout en si se mantiene.
Por eso se pueden incluir distintos patrones tratando de prevenir estos posibles cambios o
inconsistencias.

## Proceso de parseo

### Lectura de las hojas

Primeramente se lee el archivo excel completa utilizando la libreria `fast-excel`.
Esta libreria permite leer el excel de manera super eficiente en cuanto a memoria y velocidad
de procesamiento.

Luego, se pasa a leer cada hoja de manera individual, donde el nombre de la hoja representa la
carrera (mas especificamente, el `codigo de carrera`).
Cada hoja es parseada de forma independiente, ignorando las hojas irrelevantes como `codigos` o
`materias homologadas`.

### Parseo de las hojas

Primeramente se debe tratar de buscar la fila de encabezados.
Esto se hace buscando la linea que contiene en alguna de sus columnas el string `item` o
`ítem`, este numero de fila se guarda y luego se trata de buscar el numero de columna donde
comienza el encabezado (por si la tabla completa en un futuro se llegase a mover una columna
para la derecha o izquierda, entonces no se rompe el parser).

#### Eleccion del layout

A partir de este punto se asume que los encabezados estan pegados uno detras de otro, sin
espacios vacios de por medio.

Se compara cada layout conocido con la fila de encabezados.
El primer layout que coincida completamente sera el utilizado para parsear las filas
subsiguientes.

#### Parseo de filas

Cada fila luego es parseada usando el layout correspondiente, mapeando encabezados del layout
con los campos dentro de una clase DTO llamada `SubjectCsvDTO` (la razon quedara claro mas
adelante).
Los campos de dicha clase son tratados en su totalidad como strings.

Luego de completar el parseo de todas las filas del excel (se para cuando se encuentra la
primera fila completamente nula, es decir, con todas las celdas vacias), se envia la lista y se
asigna a un hashmap la carrera con la lista de materias DTO.

#### Limpieza y persistencia

Luego de contar con la lista de materias y carreras se pasa a realizar la persistencia, pero
antes pasamos nuestras materias del DTO a nuestro modelo real.

En este paso se hace la limpieza de los campos como transformacion de fechas, strings, y
desambiguacion de semestres.

##### Desambiguacion de semestres

En los archivos excel casi nunca se encuentra completa la informacion de semestre para cada
materia, entonces para tratar de resolver a cual semestre pertenece usamos los `SubjectMetada`
que contamos dentro de nuestra base de datos (es parte de una de las migraciones, donde
literalmente se encuentra cargada la malla curricular completa), esto gracias a contar con los
datos utilizados por el poliplanner original.

Si aun asi no se encuentra un semestre para dicha materia, se pone por defecto `0`,
simbolizando que dicha materia no cuenta con semestre.

Este es un paso importante para la experiencia del usuario final.

Para ver el modelo completo de la base de datos, se puede consultar:
- [Esquema de BD](media/db_scheme.png).
- [Esquema de metadatos](media/metadata_db_scheme.png)

## Diagrama de flujo

```text
+----------------------------------+
|     Leer archivo Excel completo  |
|        usando fast-excel         |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
|   Iterar sobre cada hoja del     |
|       Excel (carrera)            |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
| Ignorar hojas irrelevantes como  |
|  "codigos" o "materias homolog." |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
|  Buscar fila de encabezados:     |
|   contiene "item" o "ítem"       |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
| Comparar fila de encabezados con |
|    layouts JSON conocidos        |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
|  Seleccionar layout que coincide |
|         completamente            |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
|   Parsear filas de materias      |
|  usando layout seleccionado      |
|  -> Generar SubjectCsvDTO        |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
| Mapear DTOs al modelo real y     |
| preparar para persistencia       |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
|  Limpiar y transformar campos:   |
|  fechas, strings, símbolos (*)   |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
| Desambiguar semestres usando     |
| SubjectMetadata o asignar 0 si   |
| no se encuentra                  |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
|     Insertar materias en la      |
|          Base de datos           |
+----------------+-----------------+
                 |
                 v
+----------------------------------+
|   Commit transaccional si todo   |
|           es exitoso             |
+----------------------------------+
```
