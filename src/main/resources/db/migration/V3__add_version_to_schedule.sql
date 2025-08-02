-- Agregar la columna
ALTER TABLE schedules
ADD COLUMN schedule_sheet_version BIGINT;

ALTER TABLE schedules
ADD CONSTRAINT fk_schedule_sheet_version
FOREIGN KEY (schedule_sheet_version)
REFERENCES sheet_version(version_id);

-- Asignar un valor por defecto a los registros existentes
-- (usamos la última versión existente)
UPDATE schedules
SET schedule_sheet_version = (
    SELECT MAX(version_id) FROM sheet_version
)
WHERE schedule_sheet_version IS NULL;

-- Hacer la columna NOT NULL
ALTER TABLE schedules
ALTER COLUMN schedule_sheet_version SET NOT NULL;
