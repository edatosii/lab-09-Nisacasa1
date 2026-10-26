#!/usr/bin/env bash
# Ejecuta las pruebas del proyecto (no necesitan MySQL).
#
#   ./scripts/test.sh                                              # todas las pruebas
#   ./scripts/test.sh -Dtest='EstudianteDAOTest$Etapa1_Registrar'  # una sola etapa
#
# Cualquier argumento extra se pasa a Maven. Termina con código distinto de 0 si alguna prueba falla.
set -uo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/_comun.sh"
exigir_java

echo "Ejecutando pruebas (la primera vez tarda unos minutos mientras se descargan las dependencias)..."
if ./mvnw -B test "$@"; then
    echo
    echo "OK: todas las pruebas pasaron."
else
    codigo=$?
    echo
    echo "FALLO: hay pruebas que no pasan. Revise el detalle más arriba." >&2
    exit "$codigo"
fi
