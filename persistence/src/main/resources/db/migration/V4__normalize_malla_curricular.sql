-- 1. Normalizar códigos
UPDATE career_metadata
SET code = split_part(code, '-', 1);

-- 2. Crear tabla temporal de IDs canónicos
DROP TABLE IF EXISTS temporal;
CREATE TEMP TABLE temporal AS
SELECT code AS base_code, MIN(id) AS temporal_id
FROM career_metadata
GROUP BY code;

-- 3. Redirigir materias a ID canónico
UPDATE subjects_metadata sm
SET career_id = c.temporal_id
FROM temporal c, career_metadata cm
WHERE sm.career_id = cm.id
AND cm.code = c.base_code
AND sm.career_id <> c.temporal_id;

-- 4. Eliminar carreras duplicadas
delete from career_metadata cm
using temporal c
where cm.code = c.base_code and cm.id <> c.temporal_id;
