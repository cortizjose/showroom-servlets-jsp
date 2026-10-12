# Control de versiones del módulo

**Evidencia GA7-220501096-AA2-EV02**

> Requisito del enunciado: *"Se debe crear el proyecto utilizando herramientas de
> versionamiento"* y *"Archivo con enlace del repositorio"*.

---

## 1. Herramienta

**Git**, el estándar de la industria y el que usan GitHub, GitLab y Bitbucket.

```bash
git --version
```

## 2. Inicialización

```bash
cd showroom-servlets-jsp
git init -b main
git config user.name "jose"
git config user.email "josedavidcortesortiz.1@gmail.com"
```

## 3. Qué queda fuera del repositorio

`.gitignore` impide subir lo que no debe versionarse:

| Patrón | Motivo |
|---|---|
| `/build/`, `*.class` | Resultado de la compilación: se regenera con `compilar.bat`. Versionar binarios ensucia el historial. |
| `*.war` | Empaquetado, también regenerable. |
| `configuracion.local.properties` | Reservado para credenciales reales de otro servidor. |
| `.idea/`, `.vscode/`, `*.iml`, `nbproject/private/` | Archivos del editor, propios de cada desarrollador. |
| `Thumbs.db`, `desktop.ini`, `.DS_Store` | Basura del sistema operativo. |
| `*.log` | Registros locales. |

> **Sobre `configuracion.properties`:** sí se versiona, porque trae los valores
> de una instalación de XAMPP recién instalada (`root` sin contraseña) y es lo
> que permite que otra persona ejecute el proyecto sin adivinar la
> configuración. En un sistema real las credenciales irían en un archivo
> excluido del repositorio; subir un archivo con credenciales de producción es
> uno de los errores de seguridad más frecuentes.

Los dos `.jar` de `WEB-INF/lib` **sí** se versionan a propósito: son las únicas
dependencias del proyecto y, al no usar Maven, no hay un archivo que permita
volver a descargarlas. Van con el código para que compile sin conexión.

## 4. Convención de mensajes

Se aplicó **Conventional Commits**:

```
<tipo>(<alcance>): <descripción breve en imperativo>

<cuerpo: el detalle y, sobre todo, el porqué de cada decisión>
```

| Tipo | Uso |
|---|---|
| `chore` | Configuración y mantenimiento. |
| `feat` | Nueva funcionalidad. |
| `fix` | Corrección de un defecto. |
| `docs` | Documentación. |

**Lo que distingue este historial:** el cuerpo de cada commit explica *por qué*
se tomó la decisión, no solo qué se cambió. Un mensaje que dice «se agrega
validación» no aporta nada que el diff no muestre; uno que dice «la validación
va en el servidor porque los atributos del HTML se desactivan desde el
navegador» conserva el razonamiento.

---

## 5. Historial

| # | Hash | Mensaje |
|---|---|---|
| 1 | `0fc0618` | `chore: estructura del proyecto y script de compilacion` |
| 2 | `cc9ce1f` | `feat(bd): esquema de seis tablas y datos de ejemplo` |
| 3 | `f8c23cf` | `feat(modelo): entidades del dominio y calculo de totales` |
| 4 | `9bc8d98` | `feat(util): validador de formularios y cifrado de contrasenas` |
| 5 | `e9fda7e` | `feat(datos): conexion JDBC y DAO con consultas preparadas` |
| 6 | `d37ba3e` | `feat(seguridad): filtros de autenticacion y de codificacion` |
| 7 | `25da137` | `feat(servlets): ingreso, tablero y gestion de clientes` |
| 8 | `aa82ed7` | `feat(servlets): catalogo con control por rol y cotizaciones` |
| 9 | `bc07be3` | `feat(vistas): plantilla, hoja de estilos y vistas JSP` |
| 10 | `5b32210` | `feat(config): descriptor con rutas, filtros y paginas de error` |
| 11 | `c72b178` | `docs: informe tecnico, pruebas realizadas y guia de uso` |

El commit 12 agrega este documento y el archivo con el enlace del repositorio.

**Qué representa este orden.** Reproduce la **construcción por capas**: primero
la infraestructura, luego los datos, después la lógica, la interfaz HTTP, las
vistas y la configuración. Cada commit solo depende de los anteriores, de modo
que en cualquier punto del historial el proyecto se puede compilar.

Dicho con precisión: el repositorio se inicializó cuando el módulo ya estaba
construido y probado, y el historial organiza ese resultado por capas. No
pretende ser un registro minuto a minuto del desarrollo. Los dos defectos que
aparecieron durante las pruebas —la *locale* de JSTL y el escape de la salida—
quedaron corregidos antes del primer commit, y están documentados con su causa
raíz en [`02-pruebas-realizadas.md`](02-pruebas-realizadas.md).

Consultas útiles:

```bash
git log --oneline
git log --stat                  # archivos afectados por cada commit
git show aa82ed7                # el detalle de un cambio concreto
```

---

## 6. Publicación en GitHub

### Paso 1 — Crear el repositorio

En **github.com** → botón **New repository**:

- **Nombre:** `showroom-servlets-jsp`
- **Visibilidad:** **Public**. Es lo que permite que el instructor abra el
  enlace sin iniciar sesión y sin que se le invite al repositorio.
- **No marcar** «Add a README file», «Add .gitignore» ni «Choose a license»: el
  proyecto ya los trae y crearlos generaría un conflicto al subir.

### Paso 2 — Enlazar y subir

```bash
cd showroom-servlets-jsp
git remote add origin https://github.com/cortizjose/showroom-servlets-jsp.git
git push -u origin main
```

> **Si GitHub pide contraseña:** desde 2021 no acepta la de la cuenta. Hay que
> generar un *token* en **Settings → Developer settings → Personal access
> tokens → Tokens (classic)**, con el permiso `repo`, y usarlo como contraseña.

### Paso 3 — Verificar que el enlace funcione de verdad

Esta comprobación es la que evita el problema más común: entregar un enlace que
el instructor no puede abrir.

```bash
# Debe devolver un hash sin pedir credenciales. Si pide usuario y contrasena,
# el repositorio esta en privado.
git ls-remote https://github.com/cortizjose/showroom-servlets-jsp.git HEAD
```

Y en el navegador: abrir el enlace en una **ventana de incógnito**, sin sesión
de GitHub. Si aparece «404 — This is not the web page you are looking for», el
repositorio sigue privado o el nombre está mal escrito.

### Paso 4 — Registrar el enlace

Escribir la URL en `ENLACE-REPOSITORIO.txt`, que acompaña la entrega.

---

## 7. Flujo para continuar el proyecto

```bash
git checkout -b feature/pedidos    # rama de trabajo
# ... desarrollo y pruebas ...
git add .
git commit -m "feat(pedidos): conversion de cotizacion aprobada en pedido"
git checkout main
git merge feature/pedidos
git branch -d feature/pedidos
```

**Por qué ramas:** `main` se mantiene siempre en un estado que compila y
funciona, mientras el trabajo en curso queda aislado hasta estar probado.

---

## 8. Comandos empleados

| Comando | Función |
|---|---|
| `git init -b main` | Crea el repositorio con la rama principal `main`. |
| `git status` | Muestra lo modificado y lo pendiente. |
| `git add <ruta>` | Agrega cambios al área de preparación. |
| `git commit -m "..." -m "..."` | Registra los cambios con título y cuerpo. |
| `git log --oneline` | Historial resumido. |
| `git show <hash>` | Detalle de un commit. |
| `git remote add origin <url>` | Enlaza con el repositorio remoto. |
| `git push -u origin main` | Publica la rama. |
| `git ls-remote <url>` | Comprueba que el repositorio sea accesible sin credenciales. |

---

## 9. Evidencia del cumplimiento

| Criterio del enunciado | Evidencia |
|---|---|
| El proyecto se creó con herramientas de versionamiento | Repositorio Git con 12 commits (sección 5). |
| Historial ordenado y trazable | Conventional Commits, con el porqué de cada decisión (sección 4). |
| Información sensible protegida | `.gitignore` excluye binarios y configuración local (sección 3). |
| Proyecto reproducible por terceros | `README.md`, `compilar.bat`, `database/esquema.sql` y las dependencias versionadas. |
| Archivo con enlace del repositorio | `ENLACE-REPOSITORIO.txt` (sección 6, paso 4). |
