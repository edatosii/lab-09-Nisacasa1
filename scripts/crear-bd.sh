#!/usr/bin/env bash
# Crea la base de datos y la tabla en su MySQL, con los datos del archivo .env.
# Se ejecuta una sola vez (si ya existen, no pasa nada).
#
#   ./scripts/crear-bd.sh
#
# Le pide la contraseña de un usuario de MySQL con permiso para crear bases de datos (normalmente "root").
set -euo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/_comun.sh"
exigir_env

if ! command -v mysql >/dev/null 2>&1; then
    echo "ERROR: no se encontró el comando 'mysql'. Compruebe que MySQL Server está instalado y que su carpeta 'bin' está en el PATH." >&2
    exit 1
fi

# Lee DB_HOST, DB_PORT y DB_NAME del .env (quita \r de Windows y comillas).
valor() { grep -E "^$1[[:space:]]*=" .env | head -n 1 | cut -d= -f2- | tr -d '\r"' | sed "s/^[[:space:]]*//;s/[[:space:]]*$//"; }
host=$(valor DB_HOST); puerto=$(valor DB_PORT); nombre=$(valor DB_NAME)
if [ -z "$host" ] || [ -z "$puerto" ] || [ -z "$nombre" ]; then
    echo "ERROR: complete DB_HOST, DB_PORT y DB_NAME en el archivo .env." >&2
    exit 1
fi

read -r -p "Usuario de MySQL con permiso para crear bases de datos [root]: " usuario
usuario=${usuario:-root}

echo "Contraseña de '$usuario' (no se ve mientras la escribe):"
mysql -h "$host" -P "$puerto" -u "$usuario" -p -e "CREATE DATABASE IF NOT EXISTS \`$nombre\`; USE \`$nombre\`; SOURCE db/schema.sql;"
echo "OK: base de datos '$nombre' lista."
