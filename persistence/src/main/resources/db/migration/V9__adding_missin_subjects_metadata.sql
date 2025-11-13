-- Migración para agregar metadatos faltantes de materias

-- Ajustar secuencia para evitar conflictos de id
SELECT setval(pg_get_serial_sequence('subjects_metadata', 'id'), COALESCE(MAX(id), 1)) FROM subjects_metadata;

-- IAE
INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'IAE'), 'calculo v variable compleja', 4, 1.0, 4;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'IAE'), 'calculo vi transformadas de laplace y fourier', 4, 1.0, 5;

-- ICM (solo una materia electiva)
INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'ICM'), 'electiva', 3, 1.0, 0;

-- IEN
INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'IEN'), 'calculo v variable compleja', 4, 1.0, 4;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'IEN'), 'calculo vi transformadas de laplace y fourier', 4, 1.0, 5;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'IEN'), 'electronica, instrumentacion y control', 4, 1.0, 4;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'IEN'), 'mercado, logistica y distribucion de combustibles', 4, 1.0, 8;

-- ISP
INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'ISP'), 'algebra', 4, 1.0, 2;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'ISP'), 'dibujo tecnico', 3, 1.0, 0;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'ISP'), 'organizacion, sistemas y metodos', 3, 1.0, 0;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'ISP'), 'gestion de calidad', 3, 1.0, 7;

INSERT INTO subjects_metadata (career_id, name, credits, credits_percentage_required, semester)
SELECT (SELECT id FROM career_metadata WHERE code = 'ISP'), 'gestion de personas', 3, 1.0, 4;
