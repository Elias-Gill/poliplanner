# Conectar con Google Drive para obtener archivos públicos

Este proyecto utiliza la API pública de Google Drive para **listar archivos dentro de carpetas
públicas** y construir sus URLs de descarga.
No es necesario autenticarse con OAuth, basta con una **API Key pública de Google Cloud
Platform**.

### Listar Archivos de una Carpeta Pública

Para obtener la lista de archivos dentro de una carpeta pública de Google Drive se realiza una
petición HTTP como la siguiente:

```javascript
var folders = `https://www.googleapis.com/drive/v3/files?q='${FOLDER_IDENTIFIER}'+in+parents&key=${API_KEY}`
```

* **`${FOLDER_IDENTIFIER}`**:
  Es el identificador único de la carpeta en Google Drive.
  Se puede obtener de la URL de Drive, es el valor después de `folders/`.
* **`${API_KEY}`**:
  La API key pública generada desde Google Cloud Console.

### Ejemplo de Respuesta

La respuesta es un objeto JSON con información sobre los archivos encontrados:

```json
{
    "files": [
        {
            "kind": "drive#file",
            "id": "1-GgXSaTTQxDOmew1H3XRqHVTVghSc7ek",
            "name": "Planificación de clases y examenes .Segundo Academico 2025 versión web080725.xlsx",
            "mimeType": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        }
    ],
    "kind": "drive#fileList",
    "incompleteSearch": false
}
```

Cada objeto dentro de `files` representa un archivo disponible para descarga.

### Descargar Archivos

Para descargar un archivo público a partir de su `id`, se utiliza el siguiente patrón de URL:

```javascript
var file = `https://drive.google.com/uc?export=download&id=${fileId}`
```

### ¿Qué sucede si el ID no es válido?

Si el identificador proporcionado no corresponde a una carpeta (o es inválido), la respuesta
será simplemente una lista vacía:

```json
{
    "files": [],
    "kind": "drive#fileList",
    "incompleteSearch": false
}
```

No es necesario manejar esto como un error crítico.
El proceso simplemente ignora carpetas vacías.

### Generar una API Key para Google Drive

Debes crear una API Key en [Google Cloud Console](https://console.cloud.google.com/) y
exportarla como variable de entorno:

```bash
export GOOGLE_API_KEY=tu_api_key_aqui
```

Esta clave sólo permite **listar archivos públicos** y no da acceso a archivos privados.

### Fuente

Este método está basado en la guía original:
[Using the Google Drive API for Public Folders](https://fleker.medium.com/using-the-google-drive-api-for-public-folders-f1f7308385ad)
