-- 1. Crear tabla temporal con los duplicados
CREATE TEMP TABLE duplicates AS
SELECT id,
ROW_NUMBER() OVER (
    PARTITION BY career_id, name, semester
    ORDER BY id
) AS rn
FROM subjects_metadata;

-- 2. Borrar referencias a los duplicados en subjects_prerrequisite
DELETE FROM subjects_prerrequisite
WHERE class1_id IN (
    SELECT id FROM duplicates WHERE rn > 1
)
OR class2_id IN (
    SELECT id FROM duplicates WHERE rn > 1
);

-- 3. Borrar los duplicados en subjects_metadata
DELETE FROM subjects_metadata
WHERE id IN (
    SELECT id FROM duplicates WHERE rn > 1
);

-- 4. Borrar la tabla temporal
DROP TABLE duplicates;
