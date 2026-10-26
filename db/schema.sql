-- Esquema de la base de datos. Lo usan:
--   * MySQL (ejecútelo una vez en su base de datos; ver el README)
--   * las pruebas (H2 en memoria, modo MySQL)
-- Mantenga SQL estándar para que funcione en ambos.
CREATE TABLE IF NOT EXISTS estudiantes (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    correo   VARCHAR(100) NOT NULL UNIQUE,
    semestre INT          NOT NULL
);
