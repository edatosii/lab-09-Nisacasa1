#!/usr/bin/env python3
"""Lee los reportes de Surefire y muestra cuántas pruebas pasaron (ej.: 4/11).

- Lo imprime al final del log del paso.
- Lo agrega al resumen de la ejecución (pestaña "Summary" del workflow run).
- Publica las salidas 'aprobadas' y 'total' para otros pasos.

Si el proyecto no compila no hay reportes: el total se cuenta a partir de los
métodos @Test del código de pruebas y las aprobadas son 0.
"""
import glob
import os
import re
import xml.etree.ElementTree as ET

REPORTES = "target/surefire-reports"
FUENTES = "src/test/java"

total = fallidas = errores = omitidas = 0
detalle = []  # (estado, nombre)

for archivo in sorted(glob.glob(f"{REPORTES}/TEST-*.xml")):
    for caso in ET.parse(archivo).getroot().iter("testcase"):
        total += 1
        nombre = f"{caso.get('classname', '').split('.')[-1]}.{caso.get('name')}"
        if caso.find("failure") is not None:
            fallidas += 1
            detalle.append(("FALLA", nombre))
        elif caso.find("error") is not None:
            errores += 1
            detalle.append(("ERROR", nombre))
        elif caso.find("skipped") is not None:
            omitidas += 1
            detalle.append(("OMITIDA", nombre))
        else:
            detalle.append(("OK", nombre))

detalle.sort(key=lambda d: d[1])
compila = total > 0
if not compila:
    # Sin reportes: no compiló (o no llegó a ejecutar). Contar @Test del código de pruebas.
    for ruta in glob.glob(f"{FUENTES}/**/*.java", recursive=True):
        with open(ruta, encoding="utf-8") as f:
            total += len(re.findall(r"^\s*@Test\b", f.read(), flags=re.M))

aprobadas = total - fallidas - errores - omitidas if compila else 0
porcentaje = round(100 * aprobadas / total) if total else 0

linea = f"PRUEBAS APROBADAS: {aprobadas}/{total} ({porcentaje}%)"
borde = "=" * len(linea)
print(f"\n{borde}\n{linea}\n{borde}")
if not compila:
    print("El proyecto no compiló o las pruebas no se ejecutaron. Revise el paso anterior.")
else:
    print(f"fallidas: {fallidas} | con error: {errores} | omitidas: {omitidas}")
    for estado, nombre in detalle:
        marca = {"OK": "[ OK ]", "FALLA": "[FALLA]", "ERROR": "[ERROR]", "OMITIDA": "[SKIP]"}[estado]
        print(f"  {marca} {nombre}")

resumen = os.environ.get("GITHUB_STEP_SUMMARY")
if resumen:
    with open(resumen, "a", encoding="utf-8") as f:
        f.write(f"## Pruebas aprobadas: {aprobadas}/{total} ({porcentaje}%)\n\n")
        if not compila:
            f.write("El proyecto **no compiló** o las pruebas no se ejecutaron.\n")
        else:
            f.write("| Estado | Prueba |\n|---|---|\n")
            for estado, nombre in detalle:
                icono = {"OK": "✅ OK", "FALLA": "❌ Falla", "ERROR": "❌ Error", "OMITIDA": "⏭️ Omitida"}[estado]
                f.write(f"| {icono} | `{nombre}` |\n")

salida = os.environ.get("GITHUB_OUTPUT")
if salida:
    with open(salida, "a", encoding="utf-8") as f:
        f.write(f"aprobadas={aprobadas}\ntotal={total}\n")
