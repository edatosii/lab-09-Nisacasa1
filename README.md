# [LAB-9]: CRUD de estudiantes con Servlets y MySQL

## Prerequisitos

1. **Git**. [Link de Descarga](https://git-scm.com/install/). Descargar el instalador según su sistema operativo y seguir las instrucciones por defecto (Siguiente, siguiente, etc.).
2. **Visual Studio Code** (o su editor de preferencia). [Link de Descarga](https://code.visualstudio.com/download). Se recomienda instalar la extensión **Extension Pack for Java**.
3. Cuenta de GitHub. [Link de Registro](https://github.com/signup). Puede usar su cuenta personal o la de la universidad.
4. **JDK 17 o superior** (no basta con un JRE). [Link de Descarga](https://adoptium.net/) (Temurin 17). Compruébelo con `java -version` y `javac -version`. Si usa Mac, corra el siguiente comando en la terminal `brew install --cask temurin@17`
5. **MySQL Server 8**. [Link de Descarga](https://dev.mysql.com/downloads/mysql/) (MySQL Community Server). Anote la contraseña que elija para el usuario `root`.

## Descripción del laboratorio

Aplicación web (Java, Servlets y JSP) con un **CRUD de estudiantes** conectado a **MySQL**: **C**rear, **L**eer (listar y buscar), **A**ctualizar y **E**liminar.

Las vistas (`.jsp`), la clase `Estudiante`, la conexión a la base de datos (`ConexionBD`) y la validación del formulario ya están hechas. Usted debe completar **dos archivos**:

| Archivo | ¿Se modifica? |
|---|---|
| `src/main/java/co/umariana/dao/EstudianteDAO.java` | **Sí.** Busque los `TODO`: acceso a la base de datos con JDBC, igual que `ClienteDAO` visto en clase (Etapas 1 a 5). |
| `src/main/java/co/umariana/servlets/EstudianteServlet.java` | **Sí.** Busque los `TODO`: controlador HTTP (Etapas 6 y 7). Lea el **CONTRATO** al inicio de la clase. |
| `src/test/java/...` | **No.** Son las pruebas. |
| `.github/` | **No.** Es la automatización. |
| Todo lo demás | No es necesario modificarlo. |

| Etapa | Qué se implementa |
|---|---|
| 1 | `registrar` (INSERT) — **ya resuelta**, úsela como modelo |
| 2 | `listar` (SELECT) |
| 3 | `buscarPorId` (SELECT ... WHERE) |
| 4 | `actualizar` (UPDATE) |
| 5 | `eliminar` (DELETE) |
| 6 | `doGet`: mostrar listado y edición (el formulario vacío, `nuevo`, ya está resuelto) |
| 7 | `doPost`: actualizar y eliminar (`crear` ya está resuelto) |

Resuelva en orden: cada etapa usa las anteriores.

## Ejecutar el proyecto

Se usan tres scripts de la carpeta `scripts/`, desde una terminal **dentro de la carpeta del proyecto** (no necesita instalar Maven: el proyecto lo incluye).

| Acción | Comando |
|---|---|
| Ejecutar las pruebas | `./scripts/test.sh` |
| Crear la base de datos | `./scripts/crear-bd.sh` |
| Iniciar la aplicación | `./scripts/start.sh` |

- **macOS / Linux:** la terminal normal.
- **Windows:** use **Git Bash** (se instala junto con Git). Ábralo desde el menú Inicio, o en VS Code: *Terminal → New Terminal* y, en la flecha junto al `+`, elija **Git Bash**.

### 1. Ejecutar las pruebas

Las pruebas **no necesitan MySQL** (usan una base de datos en memoria).

```bash
./scripts/test.sh
```

La primera vez tarda unos minutos porque descarga lo necesario; las siguientes son rápidas. Al principio la mayoría fallan; su trabajo es hacerlas pasar. Al final el script indica `OK: todas las pruebas pasaron.` o `FALLO: ...`.

Para ejecutar una sola etapa, agregue el argumento entre comillas simples:

```bash
./scripts/test.sh -Dtest='EstudianteDAOTest$Etapa1_Registrar'
```

También puede subir su rama, abrir el Pull Request y agregar la etiqueta `Running Tests`: GitHub ejecuta las mismas pruebas en la nube (ver [la sección 5](#5-abrir-el-pull-request-pr)).

### 2. Configurar la conexión a MySQL

La aplicación lee su configuración del archivo **`.env`** (claves `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`; ver `conexion/ConexionBD.java`). Nunca se escriben contraseñas dentro del código ni se sube el `.env` a GitHub.

1. Copie `.env.example` a `.env` y complete **todos** los valores con los datos de su MySQL (por ejemplo `DB_HOST=localhost`, `DB_PORT=3306`, `DB_NAME=estudiantes`, `DB_USER=root` y su contraseña):

   ```bash
   cp .env.example .env
   ```

2. Cree la base de datos y la tabla (una sola vez). El script usa el `DB_NAME` de su `.env` y le pide la contraseña de `root`:

   ```bash
   ./scripts/crear-bd.sh
   ```

### 3. Ejecutar la aplicación completa (con MySQL)

```bash
./scripts/start.sh
```

Cuando el texto deje de avanzar, abra http://localhost:8080/ en su navegador. Verá la lista de estudiantes (vacía al comienzo) y podrá crear, editar y eliminar.

- Para detenerla: `Ctrl + C`.
- Si cambia el código, deténgala y vuelva a iniciarla.

## Instrucciones generales

Estas instrucciones aplican para todos los ejercicios en este curso, se recomienda aprender estos pasos.

## 1. Clonar el repositorio

Copie la URL de **su** repositorio (botón verde **Code** en GitHub) y ejecute:

```bash
git clone <URL-DE-SU-REPOSITORIO>
```

Luego abra el repositorio desde VSCode (o su editor de preferencia).

## 2. Crear una rama nueva

**No trabaje sobre `main`.** Cree una rama para su ejercicio. 

```bash
git checkout -b LAB-9-juan-perez
```

## 3. Hacer cambios, probarlos y guardarlos (commit)

1. Edite `EstudianteDAO.java` y `EstudianteServlet.java`.
2. Ejecute las pruebas **en su computador** antes de subir los cambios (`./scripts/test.sh`). También puede ejecutarlas en la nube al abrir el Pull Request (sección 5).
3. Revise qué cambió:
   ```bash
   git status
   ```
4. Agregue y guarde los cambios con un mensaje claro. El mensaje debe estar en infinitivo y responder a la pregunta: "¿Qué hizo?"

   ```bash
   git add src/main/java/co/umariana/dao/EstudianteDAO.java src/main/java/co/umariana/servlets/EstudianteServlet.java
   git commit -m "Implementar el CRUD de estudiantes"
   ```

5. Puede hacer varios commits pequeños a medida que resuelve cada ejercicio, o puede hacer unn solo commit con la solución de todos los ejercicios, eso depende de usted.

## 4. Subir la rama (push)

```bash
git push origin LAB-9-juan-perez
```

## 5. Abrir el Pull Request (PR)

Una vez subida la rama, abra un Pull Request hacia `main`.
**El paso a paso de cómo abrirlo se explica en clase.**

El PR debe cumplir este formato (una acción automática lo verifica y marca ❌ si falta algo):

- **Título:** empieza con `LAB-N`, donde N es el número de la práctica. Ejemplo: `LAB-9: CRUD de estudiantes`.
- **Descripción:** incluye una línea `Nombre: Su Nombre Completo`.

Si el formato falla, edite el título o la descripción del PR (botón **Edit**) y la validación se ejecuta de nuevo sola.

Cuando el PR esté abierto, para ejecutar las pruebas en GitHub:

1. En la columna derecha del PR, abra **Labels** y agregue **`Running Tests`**.
2. Espere a que termine (1–2 minutos). Aparecerá una etiqueta con el resultado:
   - 🟢 **`Test Passing`**: todas las pruebas pasan.
   - 🔴 **`Broken Test suite`**: alguna prueba falla o el proyecto no compila. Abra la pestaña **Checks → Pruebas** para ver el error.
3. Si falló, tiene la opción de corregir y subir nuevos cambios en otro commit.

> Las pruebas solo se ejecutan cuando usted agrega la etiqueta `Running Tests`.

---

## Evaluación

Nota de 0.0 a 5.0. Este ejercicio evalúa tanto el código como **el flujo de trabajo con Git y GitHub**.

| # | Criterio | Peso |
|---|---|---|
| 1 | Pruebas aprobadas | 40% |
| 2 | Código | 20% |
| 3 | Commits | 20% |
| 4 | Pull Request | 10% |
| 5 | Puntualidad | 10% |


