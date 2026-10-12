# Guion del video de sustentación

**Evidencia GA7-220501096-AA2-EV02 — Módulos de software codificados y probados**
Aprendiz: José Cortés · Duración prevista: **8 a 10 minutos**

> Cómo usar este guion: la columna **Qué hago** dice qué mostrar en pantalla; la
> columna **Qué digo** es texto para leer o parafrasear. No hay que memorizarlo.
> Si algo sale mal en una toma, se puede cortar y repetir solo ese bloque.

---

## Antes de grabar: lista de verificación

| | Paso |
|---|---|
| ☐ | Cerrar **Docker Desktop** si está abierto (puede ocupar el puerto 8080). |
| ☐ | Doble clic en **`iniciar-servidor.bat`**. Esperar a que abra el navegador. |
| ☐ | Comprobar que entra a `http://localhost:8080/showroom/` y aparece el login. |
| ☐ | Ingresar con `admin` / `Admin2025` y **cerrar sesión**, para empezar el video desde el login. |
| ☐ | Si ya hubo pruebas: volver a importar `database/esquema.sql` para empezar con datos limpios. |
| ☐ | Cerrar pestañas y ventanas ajenas; dejar solo el navegador, el explorador de archivos y VS Code. |
| ☐ | Poner el navegador en pantalla completa y subir el zoom a **110 %** (Ctrl + +) para que el texto se lea. |
| ☐ | Tener a mano estos datos para teclear en vivo (están más abajo, en el anexo). |
| ☐ | Grabar con **Win + G** (barra de juegos de Windows) o con la herramienta que prefiera, **con audio de micrófono**. |

**Regla de oro durante la grabación:** antes de hacer clic, decir qué va a pasar;
después del clic, señalar qué pasó. Eso es lo que convierte una demostración en
una sustentación.

---

## Bloque 1 — Presentación (0:00 – 0:40)

| Qué hago | Qué digo |
|---|---|
| Pantalla del login visible, sin tocar nada. | «Buenas tardes. Soy José David Cortés Ortiz, del programa Análisis y Desarrollo de Software. Esta es la evidencia GA7-220501096-AA2-EV02: módulos de software codificados y probados.» |
| | «Lo que voy a mostrar es el Módulo Comercial del Sistema de Showroom y Ventas, desarrollado con **Java Servlets y JSP**, corriendo sobre Apache Tomcat y MySQL. Administra los clientes del showroom, el catálogo de productos y las cotizaciones que se les entregan.» |
| | «Voy a recorrer primero cómo se levanta, después el código, y al final pruebas en vivo de que funciona, incluidas las pruebas de seguridad.» |

---

## Bloque 2 — Cómo se pone en marcha (0:40 – 2:00)

| Qué hago | Qué digo |
|---|---|
| Abrir el **panel de XAMPP**. Señalar MySQL y Tomcat encendidos. | «El módulo necesita dos servicios: MySQL para los datos y Tomcat, que es el contenedor de servlets. Los dos vienen en XAMPP.» |
| Abrir **phpMyAdmin** → base `showroom_servlets` → mostrar las **6 tablas**. | «La base de datos se crea importando un solo archivo, `database/esquema.sql`. Son seis tablas: usuarios, categorías, productos, clientes, cotizaciones y el detalle de las cotizaciones.» |
| Clic en la tabla **`usuarios`** → pestaña Examinar. | «Fíjense en algo importante: en la tabla de usuarios **no hay contraseñas**. Hay una sal y un hash. Ni yo, que soy el administrador de la base, puedo leer la contraseña de nadie.» |
| Clic en la tabla **`productos`** → señalar la columna `precio`. | «Y los precios son `DECIMAL`, no `FLOAT`. Un número de punto flotante no representa los decimales de forma exacta, y ese error se multiplica por la cantidad y se arrastra hasta el total de la cotización. Para dinero se usa decimal.» |
| Mostrar el explorador con la carpeta del proyecto. Doble clic en **`compilar.bat`**. | «Para compilar no uso Maven ni Gradle. Este proyecto tiene solo dos dependencias, que viajan dentro de él, así que compila con `javac` en cualquier equipo que tenga un JDK, sin descargar nada.» |
| Señalar en la consola `[3/5] Compilando` y `Compilacion terminada`. | «Veinte clases compiladas, cero errores. El script además copia las vistas y despliega la aplicación en Tomcat.» |
| Doble clic en **`iniciar-servidor.bat`**. Esperar que abra el navegador. | «Y con este otro arranco los servicios y abro el módulo.» |

> **Si en el video se ve que Tomcat falla**, es el problema del conector
> documentado en `docs/02-pruebas-realizadas.md`, sección 5. Ya quedó corregido
> en `server.xml`, pero si reaparece se menciona así: «este equipo tiene un
> problema con el conector NIO de Tomcat; está documentado en el informe de
> pruebas y se resuelve usando el conector NIO2».

---

## Bloque 3 — Recorrido por el código (2:00 – 4:30)

> **Esta es la parte que el instructor espera ver:** las clases Java a la vista
> en el IDE y la aplicación corriendo sobre Tomcat. El recorrido de abajo lo
> hace archivo por archivo.

**Equivalencias con la estructura vista en la formación.** Si conviene decirlo
en voz alta: *«es la misma estructura de modelo, DAO, servlet y JSP, con los
mismos roles por archivo»*.

| Estructura del ejemplo de clase | En este proyecto | Qué hace |
|---|---|---|
| `Producto.java` (paquete `modelo`) | `Cliente.java`, `Producto.java`, `Cotizacion.java` (paquete `modelo`) | Las entidades del negocio |
| `ProductoDAO.java` | `ClienteDAO.java`, `ProductoDAO.java`, `CotizacionDAO.java` | Todo el acceso a datos |
| `Conexion.java` | `ConexionBD.java` | La conexión JDBC |
| `ProductoServlet.java` | `ClienteServlet.java`, `ProductoServlet.java`, `CotizacionServlet.java` | Atienden las peticiones HTTP |
| `index.jsp`, `productos.jsp`, `editar.jsp` | `login.jsp` y 12 vistas en `WEB-INF/vistas/` | Las vistas |
| `estilos.css` | `recursos/estilos.css` | La presentación |
| `ProductoServlet?accion=editar&id=N` | `/clientes?accion=editar&id=1` | **El mismo patrón de rutas** |

**Tres cosas que este proyecto agrega** y que conviene mencionar sin alardear:
autenticación con sesión y dos roles, una transacción para guardar la
cotización con su detalle, y los totales calculados en el servidor.

Abrir el proyecto en el editor (VS Code o el IDE que prefiera) y mostrar el
árbol de carpetas.

| Qué hago | Qué digo |
|---|---|
| Señalar las carpetas `modelo`, `dao`, `servlets`, `filtros`, `util`. | «El código está separado por capas. `modelo` son las entidades del negocio, `dao` tiene todo el acceso a datos, `servlets` atiende las peticiones HTTP, `filtros` resuelve lo que debe pasar antes de cualquier servlet, y `util` las herramientas comunes.» |
| Abrir **`web.xml`**. | «Este es el descriptor de despliegue. Aquí se declara qué clase atiende qué dirección. Uso `web.xml` en vez de anotaciones para que todo el mapa del módulo —rutas, filtros, sesión y páginas de error— se lea en un solo archivo.» |
| Desplazarse a los `<filter-mapping>`. | «Dos filtros. El primero fija la codificación UTF-8 antes de que cualquier servlet lea un parámetro; si se hace después, ya no tiene efecto y los acentos llegan mal. El segundo verifica la sesión.» |
| Abrir **`FiltroAutenticacion.java`**. | «La sesión se verifica **aquí**, en un filtro, y no al inicio de cada servlet. Un filtro se ejecuta antes que todos, así que un servlet nuevo queda protegido sin que yo toque su código. Repetir la comprobación en cada método es la forma segura de que algún día falte en uno.» |
| Abrir **`LoginServlet.java`**. Señalar `doGet` y luego `doPost`. | «Aquí se ve la diferencia entre los dos métodos HTTP que pide la evidencia. `doGet` **muestra** el formulario: es una consulta, no cambia nada y se puede repetir. `doPost` **procesa** el formulario: crea la sesión.» |
| Señalar el comentario sobre POST. | «El login tiene que ser POST. Con GET, la contraseña quedaría escrita en la barra de direcciones, en el historial del navegador y en los registros del servidor.» |
| Bajar al `sendRedirect` final. | «Y fíjense que después de autenticar hago una redirección, no un `forward`. Es el patrón POST-Redirect-GET: si respondiera directo al POST, al pulsar F5 el navegador reenviaría el formulario otra vez.» |
| Abrir **`ClienteDAO.java`**, método `listar`. | «Todo el SQL vive en los DAO. Ni un servlet ni una vista tienen una consulta. Y todas son `PreparedStatement` con parámetros: nunca concateno un valor dentro del SQL. El valor viaja aparte y el motor lo trata siempre como dato, jamás como código. Eso es lo que cierra la puerta a la inyección SQL, y lo voy a demostrar más adelante.» |
| Abrir **`CotizacionDAO.java`**, método `insertar`. | «Guardar una cotización escribe en dos tablas, así que va dentro de una transacción. Si fallara la segunda escritura, el `rollback` deja la base como estaba. Sin eso podría quedar una cotización sin productos y con totales que no corresponden a nada.» |
| Abrir **`CotizacionServlet.java`**, método `armarLineas`. | «Esta es la regla más importante del módulo. Del formulario tomo **qué** producto y **cuántas** unidades. El **precio** lo busco yo en el catálogo. Si aceptara el precio que manda el navegador, cualquiera cotizaría un sofá en mil pesos editando el HTML antes de enviarlo. Lo voy a probar en vivo.» |
| Abrir **`vistas/cotizaciones/detalle.jsp`**. | «Y estas son las vistas, en JSP. Uso JSTL y lenguaje de expresiones: `c:forEach` para recorrer las listas, `fmt:formatNumber` para los montos y `c:out` para imprimir los datos del usuario, que es el que los escapa.» |
| Señalar la carpeta `WEB-INF/vistas`. | «Están dentro de `WEB-INF` a propósito: todo lo que está ahí es inalcanzable escribiendo su dirección en el navegador. Nadie puede abrir una vista sin pasar por el servlet que carga sus datos y verifica la sesión.» |
| Volver al navegador y señalar una URL como `/clientes?accion=editar&id=1`. | «Y fíjense en la forma de las direcciones: un servlet y un parámetro `accion`. Es el patrón que vimos en la formación, solo que aquí cada operación además verifica la sesión y el rol antes de ejecutarse.» |

---

## Bloque 4 — Pruebas en vivo (4:30 – 8:00)

**Esta es la parte que demuestra que el módulo funciona. Son nueve pruebas.**

### Prueba 1 — El módulo exige sesión

| Qué hago | Qué digo |
|---|---|
| En la barra de direcciones escribir `localhost:8080/showroom/tablero` y Enter. | «Voy a intentar entrar directamente al tablero, sin haber iniciado sesión.» |
| Aparece el login con el aviso. | «El filtro me devolvió al login. No se puede saltar la autenticación escribiendo la dirección.» |

### Prueba 2 — Contraseña incorrecta

| Qué hago | Qué digo |
|---|---|
| Escribir usuario `admin` y contraseña `claveMala`. Enviar. | «Pruebo con una contraseña equivocada de un usuario que sí existe.» |
| Señalar el mensaje. | «Me rechaza. Y fíjense en el mensaje: dice "usuario o contraseña incorrectos", sin precisar cuál de los dos. Es a propósito: si dijera "el usuario no existe", cualquiera podría averiguar qué usuarios hay probando nombres.» |

### Prueba 3 — Ingreso correcto y tablero

| Qué hago | Qué digo |
|---|---|
| Ingresar con `admin` / `Admin2025`. | «Ahora con las credenciales correctas.» |
| Señalar el tablero y los indicadores. | «Entró. El tablero muestra los indicadores del módulo: clientes activos, productos, productos por reponer y cotizaciones. Arriba a la derecha aparece mi nombre y mi rol: administrador.» |
| Señalar la tabla de rutas del tablero. | «Y dejé en el tablero la tabla de rutas del módulo, con los métodos GET y POST que acepta cada una.» |

### Prueba 4 — Registrar un cliente (formulario con POST)

| Qué hago | Qué digo |
|---|---|
| Menú **Clientes**. | «Aquí están los tres clientes de ejemplo.» |
| Escribir `rojas` en el buscador y Buscar. | «El buscador va por GET, y por eso la búsqueda queda en la dirección: se puede guardar en favoritos o compartir. Filtró correctamente.» |
| Limpiar. Clic en **Nuevo cliente**. Dejar todo vacío y enviar. | «Voy a enviar el formulario vacío para mostrar la validación.» |
| Señalar los mensajes. | «Me marca todos los campos obligatorios de una vez, no de uno en uno. Y esta validación ocurre **en el servidor**: los atributos del HTML ayudan al usuario, pero se desactivan desde el navegador, así que nunca son la última defensa.» |
| Llenar con los datos del **Anexo A** y enviar. | «Ahora con datos correctos.» |
| Señalar el aviso verde y la fila nueva. | «Creado. Esto fue un POST, y el servidor respondió con una redirección: por eso la dirección quedó limpia y puedo recargar sin crear un cliente duplicado.» |
| Volver a **Nuevo cliente** y escribir el documento **1032456789** (el de María Fernanda). Enviar. | «Pruebo la regla de unicidad: repito el documento de un cliente que ya existe.» |
| Señalar el mensaje. | «Lo rechaza. El documento no se puede repetir.» |

### Prueba 5 — El catálogo y la baja lógica

| Qué hago | Qué digo |
|---|---|
| Menú **Catálogo**. | «Ocho productos, con precio, existencias y estado.» |
| Señalar el **Escritorio ejecutivo** (stock 3, en naranja). | «Este aparece resaltado porque está en su nivel mínimo: el sistema avisa qué hay que reponer.» |
| Filtrar por categoría **Salas**. | «Los filtros también van por GET y se combinan: texto y categoría.» |

### Prueba 6 — Cotización con varias líneas y cálculo del IVA

| Qué hago | Qué digo |
|---|---|
| Menú **Cotizaciones** → **Nueva cotización**. | «Esta es la parte central del módulo. El formulario muestra el catálogo completo y yo escribo cantidades solo en lo que quiero cotizar.» |
| Seleccionar cliente **María Fernanda Rojas**. Cantidad **2** en *Sofá modular* y **4** en *Silla de comedor*. Observación: `Entrega en 8 dias`. | «Dos sofás y cuatro sillas. Un formulario HTML que envía varias líneas a la vez: el servlet las recibe como arreglos paralelos con `getParameterValues`.» |
| Antes de enviar, decir los números. | «Hagamos la cuenta: dos sofás de 2.890.000 son 5.780.000. Cuatro sillas de 185.000 son 740.000. Subtotal 6.520.000. El IVA del 19 % es 1.238.800. Total 7.758.800.» |
| Enviar. Señalar los totales en el detalle. | «Subtotal 6.520.000, IVA 1.238.800, total 7.758.800. Exacto. Y el número de documento, COT-000001, lo asignó el servidor de forma consecutiva.» |

### Prueba 7 — La máquina de estados

| Qué hago | Qué digo |
|---|---|
| Señalar el estado **borrador** y el único botón disponible. | «La cotización nace en borrador, y el único paso posible es enviarla al cliente. No hay más botones, porque las transiciones válidas están declaradas en el código.» |
| Clic en **Enviar al cliente**. | «Ahora está enviada, y aparecen dos opciones: aprobada o rechazada.» |
| Clic en **Marcar como aprobada**. | «La apruebo. Aprobada es un estado final: fíjense que ya no hay botones para cambiarla.» |
| Señalar el aviso de estado final. | «Sin esta máquina de estados, una petición cualquiera podría devolver a borrador una cotización ya aprobada, y el estado de los negocios dejaría de ser confiable.» |
| Menú **Cotizaciones** y luego el tablero. | «Y el monto aprobado ya aparece en el tablero.» |

### Prueba 8 — Stock insuficiente

| Qué hago | Qué digo |
|---|---|
| **Nueva cotización** → cliente cualquiera → en *Escritorio ejecutivo* escribir **99**. Enviar. | «Ahora pido 99 unidades de un producto del que solo hay 3.» |
| Señalar el mensaje. | «Me dice exactamente cuántas hay disponibles, y no guardó nada. El documento no se crea a medias.» |

### Prueba 9 — Las tres pruebas de seguridad

**9a. El precio lo pone el servidor.**

| Qué hago | Qué digo |
|---|---|
| En **Nueva cotización**, clic derecho sobre el campo de cantidad del *Sofá modular* → **Inspeccionar**. | «Esta es la prueba que más me interesa mostrar. Voy a hacer de cuenta que soy un usuario malintencionado y voy a modificar el formulario desde el navegador.» |
| En el inspector, sobre la fila del sofá, añadir un campo: clic derecho en el `<input name="productoId">` → *Edit as HTML* y agregar `<input name="precio_unitario" value="1">`. | «Le agrego al formulario un campo de precio con valor de un peso, como si intentara comprar el sofá por mil pesos.» |
| Poner cantidad 1, seleccionar cliente y enviar. | «Lo envío.» |
| Señalar el precio en el detalle. | «Y el precio que guardó es 2.890.000, el del catálogo. Ignoró por completo el que yo mandé, porque el servidor nunca confía en el precio que viene del navegador: lo busca él mismo en la base de datos.» |

**9b. Inyección SQL.**

| Qué hago | Qué digo |
|---|---|
| Ir a **Clientes** y escribir en el buscador: `' OR 1=1 --` | «Ahora una carga de inyección SQL en el buscador. Si las consultas estuvieran mal hechas, esto devolvería todos los registros o rompería la consulta.» |
| Enviar. Señalar el resultado. | «Cero resultados y ningún error: la consulta buscó, literalmente, un cliente que se llame así. El texto se trató como dato, no como código, porque la consulta es preparada.» |
| Ir a phpMyAdmin y mostrar que la tabla sigue completa. | «Y la tabla está intacta.» |

**9c. Control por rol.**

| Qué hago | Qué digo |
|---|---|
| **Salir**. Ingresar con `asesor` / `Asesor2025`. | «Cierro sesión y entro con el otro rol: asesor.» |
| Ir a **Catálogo**. Señalar que no hay botón *Nuevo producto*. | «El asesor ve el catálogo, pero no tiene el botón de crear productos.» |
| En la barra de direcciones escribir `localhost:8080/showroom/productos?accion=nuevo`. | «Pero ocultar un botón no es seguridad. Voy a escribir la dirección directamente, saltándome la interfaz.» |
| Aparece la página 403. | «**403, acceso denegado.** El servlet verifica el rol en el servidor, no en la vista. Y es un 403 y no un 401: 401 significa "no sé quién eres"; 403 significa "sé quién eres, pero no te alcanza el permiso".» |
| Mostrar que sí puede entrar a **Clientes** y **Cotizaciones**. | «El asesor sí puede gestionar clientes y cotizar, que es su trabajo.» |

---

## Bloque 5 — Versionamiento (8:00 – 9:00)

| Qué hago | Qué digo |
|---|---|
| Abrir una terminal en la carpeta del proyecto. Ejecutar `git log --oneline`. | «El proyecto está versionado con Git. Doce commits, organizados por capas: primero la infraestructura, luego los datos, la lógica, las vistas y la configuración.» |
| Ejecutar `git show 25da137` y desplazarse por el mensaje. | «Y los mensajes no dicen solo qué cambié, dicen **por qué**. Este, por ejemplo, explica por qué el login va por POST y por qué después de guardar hago una redirección.» |
| Abrir el repositorio en **github.com** en una pestaña nueva. | «Está publicado en GitHub, en un repositorio público.» |
| Abrir el mismo enlace en **ventana de incógnito**. | «Y lo compruebo en una ventana de incógnito, sin mi sesión de GitHub: así es como lo va a ver el instructor. Se abre sin pedir credenciales.» |

---

## Bloque 6 — Cierre (9:00 – 9:40)

| Qué hago | Qué digo |
|---|---|
| Abrir `docs/02-pruebas-realizadas.md`. | «Por último: el módulo no solo está codificado, está probado. Aquí están documentadas 43 verificaciones ejecutadas sobre la aplicación desplegada, con su resultado.» |
| Desplazarse a la sección de defectos. | «Y están los tres defectos que esas pruebas encontraron, con su causa y su corrección. Dos valen la pena: los montos se mostraban sin formato de moneda porque JSTL, cuando no resuelve la configuración regional, no lanza error sino que devuelve el número crudo. Y las vistas no escapaban los datos del usuario: un nombre con una comilla doble rompía el formulario. Los dos están corregidos.» |
| Volver al tablero del módulo. | «Eso es el Módulo Comercial del Showroom: formularios HTML atendidos por servlets, con GET y POST, vistas en JSP, acceso a datos con consultas preparadas, control de acceso por rol y versionamiento en Git. Muchas gracias.» |

---

## Anexo A — Datos para teclear en vivo

**Cliente nuevo (Prueba 4):**

| Campo | Valor |
|---|---|
| Documento | `1098765432` |
| Nombre | `Pedro Nel Arango` |
| Correo | `pedro.arango@correo.test` |
| Teléfono | `3001234567` |
| Ciudad | `Bucaramanga` |
| Dirección | `Calle 36 # 22-10` |

**Cotización (Prueba 6):**

| Campo | Valor |
|---|---|
| Cliente | María Fernanda Rojas |
| Sofá modular tres puestos | cantidad `2` |
| Silla de comedor | cantidad `4` |
| Observaciones | `Entrega en 8 dias` |

**Los números que hay que decir sin equivocarse:**

```
  2 x 2.890.000  =  5.780.000
  4 x   185.000  =    740.000
  Subtotal       =  6.520.000
  IVA 19 %       =  1.238.800
  TOTAL          =  7.758.800
```

**Carga de inyección SQL (Prueba 9b):** `' OR 1=1 --`

**Credenciales:**

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `Admin2025` | administrador |
| `asesor` | `Asesor2025` | asesor |

---

## Anexo B — Si algo falla durante la grabación

| Problema | Qué hacer |
|---|---|
| El navegador dice que no puede conectarse | Tomcat no está arriba. Ejecutar `iniciar-servidor.bat` y esperar 15 segundos. |
| Página de error diciendo que no hay conexión con la base | MySQL está apagado. Iniciarlo en el panel de XAMPP. |
| Los montos salen como `6520000.00` | Falta el `context-param` de la *locale* en `web.xml`. Ya está incluido; si aparece, se volvió a desplegar una versión vieja: ejecutar `compilar.bat desplegar`. |
| Tomcat no arranca y el log dice `Unable to establish loopback connection` | Es el problema del conector de este equipo. Verificar que `C:\xampp\tomcat\conf\server.xml` tenga `protocol="org.apache.coyote.http11.Http11Nio2Protocol"` en el conector del 8080. |
| El puerto 8080 está ocupado | Cerrar Docker Desktop, o cambiar el puerto en `server.xml`. |
| Quiero volver a empezar con datos limpios | Importar de nuevo `database/esquema.sql` en phpMyAdmin. Borra y recrea todo. |

---

## Anexo C — Qué NO hacer en el video

- **No leer el código línea por línea.** Mostrar el archivo, explicar la decisión, seguir.
- **No mostrar contraseñas reales** de ninguna cuenta personal. Las del módulo son de prueba y están en la documentación, así que no hay problema.
- **No dejar silencios largos** mientras carga algo: es el momento de explicar qué se va a ver.
- **No improvisar los números** de la cotización. Están en el Anexo A; si no cuadran en cámara, la demostración pierde fuerza.
- **No decir «aquí hice esto»** sin explicar por qué. La sustentación se evalúa por el criterio, no por el clic.
