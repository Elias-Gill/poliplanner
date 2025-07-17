# Conectar con Google Drive

Para listar los archivos de una carpeta:

```js
var folders = `https://www.googleapis.com/drive/v3/files?q='${FOLDER_IDENTIFIER}'+in+parents&key=${API_KEY}`
```

El identificador de la carpeta es el ultimo campo de la URL.

Esto retorna un JSON:
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

Para la descarga de archivos es bastante simple:

```js
var file = `https://drive.google.com/uc?export=download&id=${fileId}`
```

### Id no es un archivo

En caso de que el id que nos proveen no es una carpeta, entonces nos aparecera como archivo
vacio:
```json
{
    "files": [],
    "kind": "drive#fileList",
    "incompleteSearch": false
}
```

**Articulo original**:
`https://fleker.medium.com/using-the-google-drive-api-for-public-folders-f1f7308385ad`

## Requisitos

Generar una `API key` en [google cloud console](https://console.cloud.google.com/) y exportarla
como variable de entorno `GOOGLE_API_KEY`.
