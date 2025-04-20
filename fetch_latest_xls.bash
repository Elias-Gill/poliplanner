#!/bin/bash

# NOTA: requiere de tener instalado 'htmlq', grep y curl.
# Este script obtiene las URLs de los archivos Excel que contienen los horarios de exámenes desde la página web oficial.
# Aunque los nombres de los archivos son inconsistentes, la última parte de cada URL incluye una fecha (en formato ddmmyyyy),
# lo que permite comparar entre ellas para determinar cuál es el archivo más reciente.
# El script extrae las fechas de las URLs, las convierte a un formato uniforme (yyyymmdd) y las compara para encontrar
# la URL del archivo con la fecha más actual de los horarios de exámenes disponibles en la página de la Politécnica.

# Obtener las URLs de los horarios de exámenes desde la página web
urls=$(curl "https://www.pol.una.py/academico/horarios-de-clases-y-examenes/" --silent | htmlq --attribute href a | grep xls | grep -i exame)

latest_url=""
latest_date="00000000"

# Iterar sobre cada URL
while read -r url; do
    # Extraer la fecha del final de la URL usando regex (en formato ddmmyyyy)
    if [[ $url =~ ([0-9]{2})([0-9]{2})([0-9]{4})\.xlsx$ ]]; then
        # Extraer la fecha y convertirla a formato yyyymmdd para la comparación
        day="${BASH_REMATCH[1]}"
        month="${BASH_REMATCH[2]}"
        year="${BASH_REMATCH[3]}"
        file_date="${year}${month}${day}" # convertir a yyyymmdd para comparación

        # Comparar fechas (como strings, funciona porque son YYYYMMDD)
        if [[ $file_date -gt $latest_date ]]; then
            latest_date=$file_date
            latest_url=$url
        fi
    fi
done <<<"$urls"

# Imprimir la última versión encontrada
echo "$latest_url"
