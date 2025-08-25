# Sincronización Automática del Excel

El sistema mantiene actualizada la base de datos con la información de los horarios de la
facultad mediante un proceso automatizado que se ejecuta una vez al día.
El flujo comienza con la obtención del archivo Excel más reciente desde la página web de la
facultad, continúa con la descarga y el parseo de los datos, y finaliza con la inserción en la
base de datos, todo dentro de una transacción que garantiza la integridad de la información.

El primer paso consiste en realizar un scraping para identificar el archivo Excel más reciente
disponible.
Para ello se listan las fuentes y enlaces de descarga, incluyendo carpetas públicas en Google
Drive cuando corresponda.
El nombre del archivo determina la versión mediante la fecha que incorpora, por ejemplo:
**`Horario-de-clases-y-examenes-Segundo-Academico-2024-version-web-19122024.xlsx`**, donde la
secuencia **19122024** indica la fecha de la versión.
Si la fuente es Google Drive, se listan todos los enlaces y se procesan de forma equivalente,
con detalles descritos en la [guía de Google Drive](google_drive.md).

Una vez identificado el archivo más reciente, este se descarga en el backend de la aplicación
mediante una solicitud HTTP.
Si se trata de una versión nueva, la información de la base de datos se actualiza con los datos
parseados del archivo, pero los archivos Excel en sí no se conservan para almacenamiento.

El proceso de parseo ocurre inmediatamente después de la descarga.
Los datos se extraen del archivo Excel y se insertan directamente en las tablas
correspondientes de la base de datos.
Para una descripción detallada del proceso de parseo puede consultarse la
[guía de parser](parser-excel.md).

Toda la actualización ocurre dentro de una transacción.
Si durante la descarga o el parseo se produce un error, la operación completa se revierte para
asegurar que la base de datos conserve su consistencia.

La ejecución automática está programada mediante **GitHub Actions**, utilizando un cron job que
se dispara una vez al día.
El proceso se inicia mediante un endpoint protegido (`sync/ci`) cuya clave de acceso se guarda
en la variable de entorno `UPDATE_KEY`.
Adicionalmente, existe un endpoint alternativo (`sync/`) que permite la actualización manual
del archivo.

El web scraper expone la función pública

```java
public ExcelDownloadSource findLatestDownloadSource() throws IOException
```

y, con fines de prueba interna, una función con visibilidad restringida al paquete:

```java
ExcelDownloadSource findLatestDownloadSourceInDoc(Document doc)
```

Ambas devuelven un objeto descargable que contiene la fecha de creación, el nombre del archivo
y el enlace de descarga.
Cada uno de estos objetos incluye además el método

```java
public File downloadThisSource() throws IOException {}
```

que descarga el archivo y lo devuelve como un archivo temporal para su posterior procesamiento.
