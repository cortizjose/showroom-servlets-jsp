# Pruebas realizadas al módulo

**Evidencia GA7-220501096-AA2-EV02 — Módulos de software codificados *y probados***

> El enunciado pide módulos **codificados y probados**. Este documento registra
> las pruebas que se ejecutaron realmente sobre la aplicación desplegada, con su
> resultado, y los **tres defectos que encontraron**.

---

## 1. Entorno de las pruebas

| Dato | Valor |
|---|---|
| Servidor | Apache Tomcat 8.5.96 (el de XAMPP) |
| JDK | Eclipse Temurin 17.0.20.1 |
| Base de datos | MariaDB 10.4 (XAMPP), base `showroom_servlets` |
| Compilación | `compilar.bat` → 20 clases, 0 errores, 0 advertencias |
| Herramienta de prueba | `curl` con sesión persistente (cookie `JSESSIONID`) |
| Verificación de datos | Consultas directas a MySQL después de cada escritura |
| Fecha | 11 de octubre de 2026 |

**Por qué se verifica también en la base de datos:** que un servlet responda
`302` no prueba que haya guardado algo. Cada prueba de escritura se confirma
consultando la tabla.

---

## 2. Resultado global

| Indicador | Valor |
|---|---|
| Verificaciones ejecutadas | **43** |
| Aprobadas | **43** |
| Defectos encontrados y corregidos | **3** |
| Defectos abiertos | **0** |
| Vistas JSP ejercitadas | 12 de 12 |
| Servlets ejercitados | 6 de 6 |
| Métodos probados | `doGet` y `doPost` en todos los servlets que los implementan |

---

## 3. Verificaciones

### 3.1. Acceso y sesión

| # | Qué se probó | Esperado | Obtenido | |
|---|---|---|---|---|
| CP-01 | `GET /` sin sesión | Redirige al login | `302` → `/login` | Aprobado |
| CP-02 | `GET /tablero` sin sesión | El filtro bloquea | `302` → `/login?error=sesion-requerida` | Aprobado |
| CP-03 | `GET /login` | Formulario con `method="post"` | `200`, 1.556 bytes | Aprobado |
| CP-04 | `POST /login` con contraseña incorrecta | Rechazo sin revelar si el usuario existe | `401`, mensaje genérico | Aprobado |
| CP-05 | `POST /login` con credenciales válidas | Abre sesión y redirige | `302` → `/tablero`, cookie `JSESSIONID` | Aprobado |
| CP-06 | `GET /tablero` con sesión | Indicadores del módulo | `200`, 5.331 bytes | Aprobado |
| CP-40 | `GET /cerrar-sesion` y reintento | Invalida la sesión | `302`, y `/tablero` vuelve a pedir login | Aprobado |

### 3.2. Clientes

| # | Qué se probó | Esperado | Obtenido | |
|---|---|---|---|---|
| CP-07 | `GET /clientes` | Los 3 clientes sembrados | 3 documentos presentes | Aprobado |
| CP-08 | `GET /clientes?busqueda=rojas` | Solo las coincidencias | 1 cliente; los otros no aparecen | Aprobado |
| CP-09 | `POST /clientes` con datos válidos | Crea y redirige | `302` → `?mensaje=creado` | Aprobado |
| CP-10 | Consulta en MySQL tras CP-09 | El registro existe | `id=4, Pedro Nel Arango, Bucaramanga` | Aprobado |
| CP-11 | `POST /clientes` con 4 campos inválidos | Devuelve el formulario con los 4 errores | `400` y los 4 mensajes | Aprobado |
| CP-12 | `POST /clientes` con documento repetido | Rechaza por unicidad | `400`, «Ya existe otro cliente con ese documento.» | Aprobado |
| CP-13 | `GET /clientes?accion=nuevo` | Formulario vacío | `200`, 3.907 bytes | Aprobado |
| CP-14 | `GET /clientes?accion=editar&id=1` | Formulario con los datos cargados | `200`, 4.056 bytes | Aprobado |
| CP-15 | `GET /clientes?accion=editar&id=999` | No revienta | `302` → `?error=no-encontrado` | Aprobado |
| CP-16 | `POST /clientes` con `accion=desactivar` | Baja **lógica** | `activo=0` y el registro sigue en la tabla | Aprobado |

### 3.3. Catálogo

| # | Qué se probó | Esperado | Obtenido | |
|---|---|---|---|---|
| CP-17 | `GET /productos` | Los 8 productos con precio y stock | `200`, 14.713 bytes | Aprobado |
| CP-18 | `GET /productos?accion=nuevo` como administrador | Formulario de alta | `200`, 5.040 bytes | Aprobado |
| CP-19 | `GET /productos?accion=editar&id=1` | Formulario con datos | `200`, 5.193 bytes | Aprobado |

### 3.4. Cotizaciones

| # | Qué se probó | Esperado | Obtenido | |
|---|---|---|---|---|
| CP-20 | `GET /cotizaciones?accion=nueva` | Clientes y catálogo disponibles | `200`, 10.716 bytes | Aprobado |
| CP-21 | **`POST` con un `precio_unitario=1` falso añadido al formulario** | El servidor **ignora** el precio enviado | Guardó `2.890.000` y `185.000`, los del catálogo | Aprobado |
| CP-22 | Línea con `cantidad=0` en el mismo envío | Se descarta en silencio | La cotización quedó con 2 líneas, no 3 | Aprobado |
| CP-23 | Totales calculados por el servidor | subtotal `6.520.000`, IVA `1.238.800`, total `7.758.800` | Exactamente esos tres valores en MySQL | Aprobado |
| CP-24 | `borrador` → `enviada` | Transición permitida | `302` → `?mensaje=estado-actualizado` | Aprobado |
| CP-25 | `enviada` → `borrador` | **Rechazada** | `302` → `?error=transicion-invalida` | Aprobado |
| CP-26 | `enviada` → `aprobada` | Transición permitida | `302` → `?mensaje=estado-actualizado` | Aprobado |
| CP-27 | `aprobada` → `enviada` | **Rechazada**: es estado final | `302` → `?error=transicion-invalida` | Aprobado |
| CP-28 | `GET` detalle de la cotización | Líneas y montos con formato de moneda | `200`, `$ 6.520.000`, `$ 1.238.800`, `$ 7.758.800` | Aprobado |
| CP-29 | `GET` detalle de una cotización inexistente | No revienta | `302` → `?error=no-encontrada` | Aprobado |
| CP-33 | Cantidad mayor que las existencias | Informa cuántas hay | `400`, «Solo hay 3 unidades de Escritorio ejecutivo.» | Aprobado |
| CP-34 | Cotización sin ninguna línea | Exige al menos un producto | `400`, mensaje correspondiente | Aprobado |
| CP-35 | Cotización sin cliente | Campo obligatorio | `400`, «El campo cliente es obligatorio.» | Aprobado |

> Tras CP-33, CP-34 y CP-35 se consultó la tabla: **seguía habiendo una sola
> cotización**. Ningún rechazo dejó un documento a medias.

### 3.5. Roles y autorización

| # | Qué se probó | Esperado | Obtenido | |
|---|---|---|---|---|
| CP-36 | `POST /login` como `asesor` | Sesión válida | `302` → `/tablero` | Aprobado |
| CP-37 | `GET /productos` como asesor | Ve el catálogo, sin botón de crear | `200`, el botón no aparece | Aprobado |
| CP-38 | `POST /productos` como asesor | **`403`** y nada escrito | `403`; el producto `HACK-01` no existe en la tabla | Aprobado |
| CP-39 | `GET /productos?accion=nuevo` como asesor | `403` | `403`, página de error propia | Aprobado |

**Lo que prueba CP-38:** que ocultar el botón en la vista no es la protección.
La peticíon se envió directamente, sin pasar por la interfaz, y el servlet la
rechazó igual.

### 3.6. Seguridad de los datos

| # | Qué se probó | Esperado | Obtenido | |
|---|---|---|---|---|
| CP-30 | Carga de inyección SQL `' OR 1=1 --` en el buscador | Se trata como texto | `200` con 0 resultados; la tabla intacta (4 registros) | Aprobado |
| CP-31 | Guardar `<script>alert(1)</script>Ana Gomez` como nombre | Las etiquetas se eliminan | Quedó `alert(1)Ana Gomez`, sin etiquetas | Aprobado |
| CP-32 | Guardar `Ana" onfocus="alert(1)` y mostrarlo en un formulario | La comilla se escapa | `value="Ana&#034; onfocus=&#034;alert(1)"`: el atributo no se rompe | Aprobado |

### 3.7. Páginas de error y recursos

| # | Qué se probó | Esperado | Obtenido | |
|---|---|---|---|---|
| CP-41 | `GET /ruta-que-no-existe` | Página 404 propia | `404` con el diseño del módulo | Aprobado |
| CP-42 | Acceso prohibido | Página 403 propia | `403`, «Acceso denegado» | Aprobado |
| CP-43 | `GET /recursos/estilos.css` | El filtro permite los recursos estáticos | `200`, 11.736 bytes | Aprobado |

---

## 4. Defectos encontrados y corregidos

### DEF-01 — El script de compilación fallaba con rutas que tienen espacios

| | |
|---|---|
| **Detectado en** | La primera ejecución de `compilar.bat` |
| **Síntoma** | `error: invalid flag: C:\Users\Jose` y, al corregir las comillas, `file not found: C:UsersJose Cortes...` |
| **Causa raíz** | Dos problemas encadenados: el listado de archivos no iba entre comillas, y dentro de un *archivo de argumentos* `javac` interpreta la barra invertida como carácter de escape, de modo que `C:\Users` se leía como `C:Users`. |
| **Corrección** | El listado escribe cada ruta entre comillas y con barras normales. |
| **Verificación** | Compilación limpia: 20 clases, 0 errores. |

### DEF-02 — Los montos aparecían sin formato de moneda

| | |
|---|---|
| **Detectado en** | CP-28, al revisar el HTML del detalle |
| **Síntoma** | El total se imprimía `6520000.00` en lugar de `$ 6.520.000`, en todas las vistas. |
| **Causa raíz** | Cuando JSTL no logra resolver una *locale* de formato, **no lanza error**: devuelve el valor con `toString()`. Las peticiones de prueba llegaban sin encabezado `Accept-Language`. |
| **Corrección** | Se declaró `javax.servlet.jsp.jstl.fmt.locale = es_CO` como `context-param` en `web.xml`. |
| **Verificación** | CP-28 repetido: `$ 6.520.000`, `$ 1.238.800`, `$ 7.758.800`. |

**Por qué importa:** es un defecto silencioso. No hay excepción ni registro en
el log; solo números mal presentados, y basta un navegador con otro idioma para
que aparezca.

### DEF-03 — Las vistas no escapaban los datos del usuario

| | |
|---|---|
| **Detectado en** | CP-31, al analizar qué pasaba con el valor guardado |
| **Síntoma** | Un nombre con una comilla doble se salía del atributo `value` de un formulario, lo que permite inyectar atributos HTML. |
| **Causa raíz** | Una expresión EL suelta como `${cliente.nombre}` **no escapa nada**. Se estaba confiando solo en el filtrado de entrada, que elimina etiquetas pero no comillas. |
| **Corrección** | Las 40 expresiones que imprimen datos del usuario pasaron a `<c:out>`, que escapa `<`, `>`, `&`, `'` y `"`. El mensaje de confirmación en JavaScript se volvió genérico, porque las reglas de escape de JS son distintas y `c:out` no sirve ahí. |
| **Verificación** | CP-32: la comilla sale como `&#034;` y el atributo queda intacto. |

**Lo que enseña este defecto:** filtrar la entrada y escapar la salida son dos
cosas distintas, y hacen falta las dos. Quitar etiquetas al guardar no protege
el atributo de un formulario; escapar al mostrar sí.

---

## 5. Notas del entorno donde se probó

Dos obstáculos del equipo de desarrollo, ajenos al código, que conviene dejar
anotados porque pueden reaparecer:

**El puerto 8080 estaba ocupado por Docker Desktop.** Tomcat arrancaba pero su
conector no podía tomar el puerto. Para probar se usó una instancia de Tomcat
aparte en el **8084**, con su propio `CATALINA_BASE`, sin modificar la
configuración de XAMPP.

**El conector NIO no arranca en este equipo.** Falla con
`Unable to establish loopback connection`, porque la máquina virtual de Java no
puede crear el *pipe* de loopback que necesita un `Selector`. Se comprobó que es
del equipo y no del proyecto: un programa de cuatro líneas que solo llama a
`Selector.open()` falla igual. La solución aplicada fue usar el conector
**NIO2**, que en Windows se apoya en IOCP y no necesita ese *pipe*:

```xml
<Connector port="8080" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
           connectionTimeout="20000" redirectPort="8443" />
```

Si al desplegar en otro equipo el log muestra ese mismo error, basta cambiar el
atributo `protocol` del conector en `conf/server.xml` por el de la línea
anterior. En un Tomcat sin ese problema, el conector `HTTP/1.1` por omisión
funciona sin tocar nada.

---

## 6. Lo que no se probó

Con el mismo criterio con que se informa lo que pasó, conviene decir qué quedó
fuera:

| Sin probar | Por qué |
|---|---|
| Página de error 500 | Habría requerido provocar una falla real de MySQL en mitad de una operación. El `error-page` está declarado y la página existe, pero no se ejercitó. |
| Concurrencia en el número consecutivo | El cálculo va dentro de la transacción, que es la defensa correcta, pero no se simularon dos usuarios cotizando en el mismo instante. |
| Navegadores | Las pruebas se hicieron con `curl` sobre el HTML generado. La hoja de estilos es propia y responde a pantallas pequeñas, pero no se verificó en varios navegadores. |
| Volumen | Se probó con 8 productos y 6 clientes. No se midió el comportamiento con miles de registros. |
