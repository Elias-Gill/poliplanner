-- 1. Actualizar nombres existentes directamente
UPDATE subjects_metadata 
SET name = LOWER(TRANSLATE(name, 
        'áéíóúÁÉÍÓÚñÑüÜ', 
        'aeiouAEIOUnNuU'));

-- 2. Eliminar caracteres especiales (opcional)
UPDATE subjects_metadata
SET name = REGEXP_REPLACE(name, '[^a-z0-9 ]', '', 'g');

-- 3. Normalizar espacios
UPDATE subjects_metadata
SET name = TRIM(REGEXP_REPLACE(name, '\s+', ' ', 'g'));
