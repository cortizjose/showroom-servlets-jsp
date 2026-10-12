# Módulo Comercial del Showroom — Java Servlets y JSP

**Evidencia GA7-220501096-AA2-EV02 — Módulos de software codificados y probados**
Actividad de aprendizaje GA7-220501096-AA2: *Aplicar estándares de codificación, de acuerdo con el diseño.*
Programa: Análisis y Desarrollo de Software — SENA. Aprendiz: **José Cortés**.

---

## 1. Qué es este proyecto

Módulo web de gestión comercial para un showroom de muebles, construido con
**Java Servlets y JSP** sobre **Apache Tomcat** y **MySQL**. Administra:

- **Clientes** que visitan el showroom (registro, consulta, edición, baja lógica).
- **Catálogo** de productos con existencias y nivel mínimo de reposición.
- **Cotizaciones** de varias líneas, con cálculo de IVA en el servidor y máquina de estados.

Hay dos roles: **administrador** (todo, incluido modificar el catálogo) y
**asesor** (clientes y cotizaciones; el catálogo solo lo consulta).

---

## 2. Puesta en marcha

**Requisitos:** XAMPP (aporta Tomcat 8.5 y MySQL) y un JDK 8 o superior.

### Paso 1 — Crear la base de datos

Abrir `http://localhost/phpmyadmin` → pestaña **Importar** → seleccionar
`database/esquema.sql` → **Continuar**.

O por consola:

```bash
C:\xampp\mysql\bin\mysql.exe -u root < database\esquema.sql
```

El script crea la base `showroom_servlets`, sus seis tablas y los datos de ejemplo.

### Paso 2 — Compilar y desplegar

```bash
compilar.bat desplegar
```

El script compila las clases, copia las vistas y deja la aplicación en
`C:\xampp\tomcat\webapps\showroom`. Si su JDK está en otra ruta, edite la
variable `JAVA_INICIO` en las primeras líneas de `compilar.bat`.

> También puede desplegar `showroom.war` copiándolo a `C:\xampp\tomcat\webapps\`.

### Paso 3 — Iniciar los servicios

En el panel de control de XAMPP, **Start** en **MySQL** y en **Tomcat**.

### Paso 4 — Abrir el módulo

```
http://localhost:8080/showroom/
```

### Cuentas de prueba

| Usuario | Contraseña | Rol | Puede |
|---|---|---|---|
| `admin` | `Admin2025` | administrador | Todo, incluido crear y editar productos |
| `asesor` | `Asesor2025` | asesor | Clientes y cotizaciones; consulta el catálogo |

---

## 3. Lo que pide la evidencia y dónde está

| Requisito del enunciado | Dónde se cumple |
|---|---|
| **Formularios HTML con servlets** | 6 formularios en `src/main/webapp` atendidos por 6 servlets en `src/main/java/.../servlets/` |
| **Método GET** | `doGet` en los 6 servlets: muestra listados, formularios y filtros de búsqueda |
| **Método POST** | `doPost` en 4 servlets: login, guardar cliente, guardar producto, crear cotización y cambiar estado |
| **Elementos de JSP** | Directivas `page`/`taglib`/`include`, EL, JSTL (`c`, `fmt`, `fn`), `jsp:include`, scriptlets, expresiones, objetos implícitos y `isErrorPage` |
| **Herramientas de versionamiento** | Repositorio Git con historial por capas (ver `docs/03-control-de-versiones.md`) |
| **Artefactos del ciclo de software** | Diagrama de clases, casos de uso e historias de usuario en `docs/01-informe-tecnico.md` |
| **Archivo con enlace del repositorio** | `ENLACE-REPOSITORIO.txt` |
| **Módulo probado** | 43 verificaciones ejecutadas y documentadas en `docs/02-pruebas-realizadas.md` |

---

## 4. Mapa de rutas

| Ruta | Servlet | GET | POST |
|---|---|---|---|
| `/login` | `LoginServlet` | Muestra el formulario | Autentica y abre la sesión |
| `/cerrar-sesion` | `CerrarSesionServlet` | Invalida la sesión | — |
| `/tablero` | `TableroServlet` | Indicadores del módulo | — |
| `/clientes` | `ClienteServlet` | Listar, filtrar, formulario | Guardar, desactivar |
| `/productos` | `ProductoServlet` | Listar, filtrar, formulario | Guardar (solo administrador) |
| `/cotizaciones` | `CotizacionServlet` | Listar, nueva, detalle | Crear, cambiar estado |

---

## 5. Arquitectura

```
Navegador (formularios HTML)
        │
        ▼
   Filtros  ──► FiltroCodificacion (UTF-8) ──► FiltroAutenticacion (sesión)
        │
        ▼
   Servlet  ──► recibe la petición, valida y decide
        │
        ├──► Validador ......... reglas de los datos de entrada
        ├──► DAO ............... todo el SQL, con consultas preparadas
        │        │
        │        ▼
        │     MySQL
        ▼
   Vista JSP ──► solo presenta; no consulta la base de datos
```

```
showroom-servlets-jsp/
├── compilar.bat                  Compila y despliega sin Maven
├── database/esquema.sql          Base de datos y datos de ejemplo
├── docs/                         Informe técnico, pruebas y versionamiento
├── showroom.war                  Aplicación empaquetada, lista para desplegar
└── src/main/
    ├── java/co/edu/sena/showroom/
    │   ├── modelo/               Usuario, Cliente, Producto, Cotizacion, DetalleCotizacion
    │   ├── dao/                  ConexionBD y un DAO por entidad
    │   ├── servlets/             Los 6 servlets del módulo
    │   ├── filtros/              Autenticación y codificación
    │   └── util/                 Validador y Seguridad
    ├── resources/                configuracion.properties (conexión a la base)
    └── webapp/
        ├── index.jsp, login.jsp
        ├── recursos/estilos.css
        └── WEB-INF/
            ├── web.xml           Descriptor: rutas, filtros, errores, sesión
            ├── lib/              JSTL y driver JDBC de MySQL
            └── vistas/           Vistas protegidas (no accesibles por URL directa)
```

**Por qué las vistas están en `WEB-INF/vistas`:** todo lo que está bajo
`WEB-INF` es inalcanzable escribiendo su dirección en el navegador. Así nadie
puede abrir una vista sin pasar por su servlet, que es el que carga los datos y
verifica la sesión.

---

## 6. Seis decisiones de diseño que conviene poder explicar

**Los precios los pone el servidor.** El formulario de cotización envía *qué*
producto y *cuántas* unidades; el precio lo busca `CotizacionServlet` en el
catálogo. Si se enviara desde el navegador, cualquiera cotizaría un sofá en mil
pesos editando el HTML. Está probado: la verificación CP-21 envía un precio
falso y el sistema lo ignora.

**Después de cada POST hay una redirección.** Es el patrón
POST-Redirect-GET. Si el servlet respondiera directamente al POST, al pulsar F5
el navegador reenviaría el formulario y crearía un registro duplicado.

**El dinero se maneja con `BigDecimal`, no con `double`.** Un `double` no
representa exactamente 0.1, y ese error se multiplica por la cantidad y se
arrastra hasta el total. En la base, las columnas son `DECIMAL`.

**La autenticación se verifica en un filtro, no en cada servlet.** Un filtro se
ejecuta antes que cualquier servlet, de modo que un servlet nuevo queda
protegido sin tocar su código. Repetir la comprobación en cada `doGet` es la vía
segura para que algún día falte en uno.

**Las bajas son lógicas.** Borrar un cliente rompería las cotizaciones que lo
referencian. Se marca inactivo: no admite documentos nuevos, pero el historial
queda íntegro.

**Todo el SQL usa `PreparedStatement` con parámetros.** Nunca se concatena un
valor dentro de la sentencia. El valor viaja aparte y el motor lo trata siempre
como dato, jamás como código: es lo que cierra la puerta a la inyección SQL.
Está probado con una carga de inyección en el buscador.

---

## 6.1. Si algo no arranca

| Síntoma | Causa y solución |
|---|---|
| El navegador no abre `localhost:8080` | Otro programa tiene el puerto. En el equipo de desarrollo lo ocupa **Docker Desktop**. Cambie el puerto del conector en `conf\server.xml` de Tomcat (por ejemplo a `8084`) o cierre el otro programa. |
| El log de Tomcat dice `Unable to establish loopback connection` | La máquina virtual de Java no puede crear el *pipe* que necesita el conector NIO. Cambie el conector a NIO2 en `conf\server.xml`: `protocol="org.apache.coyote.http11.Http11Nio2Protocol"`. Explicado en `docs/02-pruebas-realizadas.md`, sección 5. |
| `No se encontro el driver JDBC de MySQL` | Falta `mysql-connector-j-8.4.0.jar` en `WEB-INF/lib`. |
| La página de error dice que no hay conexión con la base | MySQL no está iniciado en XAMPP, o no se importó `database/esquema.sql`. |
| Los montos aparecen como `6520000.00` | Falta el `context-param` `javax.servlet.jsp.jstl.fmt.locale` en `web.xml`. |

---

## 7. Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 8+ (compilado con `--release 8`, probado con Temurin 17) |
| Web | Java Servlet 3.1 y JSP 2.3 |
| Vistas | JSP con JSTL 1.2 y lenguaje de expresiones |
| Servidor | Apache Tomcat 8.5 (el que incluye XAMPP) |
| Base de datos | MySQL / MariaDB 10.4 |
| Acceso a datos | JDBC con `PreparedStatement`, patrón DAO |
| Contraseñas | SHA-256 con sal por usuario |
| Construcción | `javac` mediante `compilar.bat`; sin Maven ni Gradle |
| Dependencias | Solo dos: JSTL y el driver de MySQL, incluidas en `WEB-INF/lib` |

---

## 8. Documentación

| Documento | Contenido |
|---|---|
| [`docs/01-informe-tecnico.md`](docs/01-informe-tecnico.md) | Historias de usuario, casos de uso, diagrama de clases, modelo de datos y plan de trabajo. |
| [`docs/02-pruebas-realizadas.md`](docs/02-pruebas-realizadas.md) | Las 43 verificaciones ejecutadas, con su resultado, y los 3 defectos que encontraron. |
| [`docs/03-control-de-versiones.md`](docs/03-control-de-versiones.md) | Flujo de trabajo con Git e historial de commits. |
