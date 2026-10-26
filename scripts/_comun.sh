#!/usr/bin/env bash
# Funciones comunes de los scripts (se cargan con "source"; no se ejecuta solo).

# Siempre trabajar desde la raíz del proyecto, sin importar desde dónde se ejecute el script.
cd "$(dirname "${BASH_SOURCE[0]}")/.."

exigir_java() {
    local version
    # En macOS "javac" puede existir sin que haya un JDK instalado: se ejecuta de verdad.
    if ! version=$(javac -version 2>&1); then
        echo "ERROR: no se encontró el JDK (el comando 'javac' no funciona). Instale JDK 17 o superior: https://adoptium.net/" >&2
        echo "       Un JRE o solo 'java' no basta. Compruébelo con:  javac -version" >&2
        echo "       Si 'javac -version' muestra otro error (por ejemplo \"Bad CPU type\"), instaló la versión equivocada para su procesador:" >&2
        echo "       en una Mac con chip Apple (M1, M2...) descargue la versión \"aarch64\". Mensaje recibido:" >&2
        echo "       $version" >&2
        exit 1
    fi
    local mayor
    mayor=$(echo "$version" | sed -E 's/^javac (1\.)?([0-9]+).*/\2/')
    if [ "$mayor" -lt 17 ] 2>/dev/null; then
        echo "ERROR: se encontró $version, pero se necesita JDK 17 o superior: https://adoptium.net/" >&2
        exit 1
    fi
}

exigir_env() {
    if [ ! -f .env ]; then
        echo "ERROR: falta el archivo .env." >&2
        echo "       Copie .env.example a .env y complete los valores:  cp .env.example .env" >&2
        exit 1
    fi
}
