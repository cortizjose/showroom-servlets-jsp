# Guion del video de sustentación

**Evidencia GA7-220501096-AA2-EV02 — Módulos de software codificados y probados**
Aprendiz: José Cortés · Duración prevista: **8 a 10 minutos**

> Cada paso tiene tres columnas: **Pantalla** es lo que debe estar visible,
> **Señalo** es dónde poner el cursor mientras se habla, y **Digo** es el texto
> para leer o parafrasear. No hay que memorizar nada. Si una toma sale mal, se
> corta y se repite solo ese bloque.

---

## Antes de grabar: lista de verificación

| | Paso |
|---|---|
| ☐ | Cerrar **Docker Desktop** si está abierto (puede ocupar el puerto 8080). |
| ☐ | Doble clic en **`iniciar-servidor.bat`**. Esperar a que abra el navegador. |
| ☐ | Comprobar que entra a `http://localhost:8080/showroom/` y aparece el login. |
| ☐ | Entrar con `admin` / `Admin2025` y **cerrar sesión**, para empezar el video desde el login. |
| ☐ | Si hubo ensayos: volver a importar `database/esquema.sql` para empezar con datos limpios. |
| ☐ | **VS Code** abierto con las 7 pestañas del bloque 3, y **clic en la primera** (`Cliente.java`) para que quede activa. |
| ☐ | En VS Code, comprobar que se vean los **números de línea** (`Ctrl+,` y buscar *line numbers*). |
| ☐ | Tener abiertos también: el panel de XAMPP, **phpMyAdmin en `http://localhost:8081/phpmyadmin/`** y una terminal en la carpeta del proyecto. |
| ☐ | Cerrar pestañas y ventanas ajenas. Dejar solo navegador, explorador de archivos y VS Code. |
| ☐ | Zoom del navegador al **110 %** (`Ctrl` + `+`) y de VS Code a **120 %**. |
| ☐ | Grabar con **Win + G**, con audio de micrófono. |

**Regla de oro:** antes de hacer clic, decir qué va a pasar; después del clic,
señalar qué pasó. Eso convierte una demostración en una sustentación.

**Cómo señalar:** llevar el cursor despacio hasta el punto y **dejarlo quieto**
mientras se habla. En un video comprimido, un cursor que se mueve rápido no se
sigue. En el código, también sirve hacer clic en el número de línea para
seleccionarla entera.

---

## Bloque 1 — Presentación (0:00 – 0:40)

| Pantalla | Señalo | Digo |
|---|---|---|
| Navegador en `localhost:8080/showroom/login`, pantalla completa. | Nada. Cursor quieto. | «Buenas tardes. Soy José David Cortés Ortiz, del programa Análisis y Desarrollo de Software. Esta es la evidencia GA7-220501096-AA2-EV02: módulos de software codificados y probados.» |
| La misma. | El título de la tarjeta de ingreso. | «Lo que voy a mostrar es el Módulo Comercial del Sistema de Showroom y Ventas, desarrollado con **Java Servlets y JSP**, corriendo sobre Apache Tomcat y MySQL. Administra los clientes del showroom, el catálogo y las cotizaciones.» |
| La misma. | La barra de direcciones, `localhost:8080`. | «Voy a recorrer tres cosas: cómo se levanta, el código por dentro, y pruebas en vivo de que funciona, incluidas las de seguridad.» |

---

> **Sobre phpMyAdmin en este equipo.** Responde en
> **`http://localhost:8081/phpmyadmin/`**, no en la dirección habitual sin
> puerto: el puerto 80 lo tiene tomado IIS, un servicio de Windows que no
> conviene apagar, así que Apache quedó configurado en el 8081.
> `iniciar-servidor.bat` lo levanta solo. Si prefiere una ventana más limpia
> para grabar, **HeidiSQL** también está instalado y sirve igual: conexión a
> `127.0.0.1`, usuario `root`, sin contraseña, puerto `3306`.

## Bloque 2 — Cómo se pone en marcha (0:40 – 2:00)

| Pantalla | Señalo | Digo |
|---|---|---|
| **Panel de control de XAMPP**. | Las filas de **MySQL**, **Apache** y **Tomcat**, con su botón en *Stop* (es decir, encendidos). | «El módulo necesita MySQL para los datos y Tomcat, que es el contenedor de servlets. Apache solo lo uso para abrir phpMyAdmin y mostrarles la base. Los tres vienen en XAMPP.» |
| **phpMyAdmin** (`localhost:8081/phpmyadmin`), base `showroom_servlets` seleccionada. | La lista de las **6 tablas** en la columna izquierda, de arriba abajo. | «La base se crea importando un solo archivo, `database/esquema.sql`. Son seis tablas: usuarios, categorías, productos, clientes, cotizaciones y el detalle de las cotizaciones.» |
| phpMyAdmin → tabla **`usuarios`** → pestaña *Examinar*. | Las columnas **`sal`** y **`contrasena_hash`** de la primera fila. | «Fíjense en algo importante: aquí **no hay contraseñas**. Hay una sal y un hash. Ni yo, que administro la base, puedo leer la contraseña de nadie.» |
| phpMyAdmin → tabla **`productos`** → pestaña *Estructura*. | La fila de la columna **`precio`**, donde dice `decimal(12,2)`. | «Y los precios son `DECIMAL`, no `FLOAT`. Un número de punto flotante no representa los decimales de forma exacta, y ese error se multiplica por la cantidad y se arrastra hasta el total. Para dinero se usa decimal.» |
| **Explorador de archivos** en la carpeta del proyecto. | El archivo **`compilar.bat`**. | «Para compilar no uso Maven ni Gradle: este proyecto tiene solo dos dependencias y viajan dentro de él, así que compila con `javac` en cualquier equipo con un JDK, sin descargar nada.» |
| Doble clic en `compilar.bat`. Consola abierta. | La línea **`[3/5] Compilando`** y luego **`Compilacion terminada`**. | «Veinte clases compiladas, cero errores. El script además copia las vistas y despliega la aplicación en Tomcat.» |
| Doble clic en **`iniciar-servidor.bat`**. | La línea final, con la URL y las credenciales. | «Y con este arranco los servicios y abro el módulo: verifica MySQL, levanta Tomcat y abre el navegador solo.» |

---

## Bloque 3 — Recorrido por el código (2:00 – 4:30)

> **Esta es la parte que el instructor espera ver:** las clases Java a la vista
> en el editor y la aplicación corriendo sobre Tomcat.

**Equivalencias con la estructura vista en la formación.** Conviene decirlo en
voz alta al abrir el editor: *«es la misma estructura de modelo, DAO, servlet y
JSP, con los mismos roles por archivo»*.

| Estructura del ejemplo de clase | En este proyecto | Qué hace |
|---|---|---|
| `Producto.java` (paquete `modelo`) | `Cliente.java`, `Producto.java`, `Cotizacion.java` | Las entidades del negocio |
| `ProductoDAO.java` | `ClienteDAO.java`, `ProductoDAO.java`, `CotizacionDAO.java` | Todo el acceso a datos |
| `Conexion.java` | `ConexionBD.java` | La conexión JDBC |
| `ProductoServlet.java` | `ClienteServlet.java`, `ProductoServlet.java`, `CotizacionServlet.java` | Atienden las peticiones HTTP |
| `index.jsp`, `productos.jsp`, `editar.jsp` | `login.jsp` y 12 vistas en `WEB-INF/vistas/` | Las vistas |
| `estilos.css` | `recursos/estilos.css` | La presentación |
| `ProductoServlet?accion=editar&id=N` | `/clientes?accion=editar&id=1` | **El mismo patrón de rutas** |

**Las 7 pestañas ya están abiertas en VS Code, en el orden del recorrido.** Se
avanza de izquierda a derecha haciendo clic en cada una.

### 3.1. El árbol del proyecto

| Pantalla | Señalo | Digo |
|---|---|---|
| VS Code, **explorador lateral desplegado** (`Ctrl+B`), carpeta `src/main/java/co/edu/sena/showroom` expandida. | Una por una, de arriba abajo: `modelo`, `dao`, `servlets`, `filtros`, `util`. | «El código está separado por capas. `modelo` son las entidades del negocio, `dao` tiene todo el acceso a datos, `servlets` atiende las peticiones HTTP, `filtros` lo que ocurre antes de cualquier servlet, y `util` las herramientas comunes.» |
| El mismo árbol, con `src/main/webapp` expandido. | La carpeta **`WEB-INF/vistas`**. | «Las vistas están dentro de `WEB-INF` a propósito: todo lo que está ahí es inalcanzable escribiendo su dirección en el navegador. Nadie abre una vista sin pasar por el servlet que carga sus datos y verifica la sesión.» |

### 3.2. Pestaña 1 — `Cliente.java`, la clase modelo

| Pantalla | Señalo | Digo |
|---|---|---|
| Pestaña `Cliente.java`, principio del archivo. | La **línea 15** y las siguientes: `private String documento`, `nombre`, `correo`… | «Esta es una clase modelo: solo atributos privados con sus métodos de acceso. No sabe nada de la base de datos ni del protocolo HTTP, así que puede viajar del DAO al servlet y del servlet a la vista sin arrastrar dependencias.» |
| El mismo archivo, bajar a la **línea 93**. | El método **`isNuevo()`**. | «Aquí hay una decisión pequeña pero útil: la clase misma responde si el objeto ya se guardó. Así el mismo formulario sirve para crear y para editar, sin dos vistas casi iguales que mantener en paralelo.» |

### 3.3. Pestaña 2 — `ClienteDAO.java`, el acceso a datos

| Pantalla | Señalo | Digo |
|---|---|---|
| Pestaña `ClienteDAO.java`, **línea 41** a la vista. | La línea `conexion.prepareStatement(...)` y, debajo, la **línea 45**, `sentencia.setString(1, patron)`. | «Todo el SQL vive en los DAO: ni un servlet ni una vista tienen una consulta. Y todas son consultas preparadas con parámetros. Nunca concateno un valor dentro del SQL.» |
| El mismo archivo, **líneas 23 a 26**. | El comentario que explica el comodín `%`. | «El comodín va en el **valor** del parámetro, no en la sentencia. Así el texto que escribe el usuario viaja aparte y el motor lo trata siempre como dato, jamás como código. Eso es lo que cierra la puerta a la inyección SQL, y lo voy a demostrar más adelante.» |
| El mismo archivo, **línea 157**. | El método **`desactivar`** y el comentario de la línea 151. | «Y fíjense que no hay ningún `DELETE`. Borrar un cliente rompería las cotizaciones que lo referencian: se marca inactivo, y el historial queda íntegro.» |

### 3.4. Pestaña 3 — `ConexionBD.java`, la conexión JDBC

| Pantalla | Señalo | Digo |
|---|---|---|
| Pestaña `ConexionBD.java`, **línea 30** a la vista. | El bloque **`static {`** y la **línea 48**, `Class.forName(...)`. | «Esta es la clase de conexión. El driver se registra una sola vez, cuando la clase se carga, y no en cada petición.» |
| El mismo archivo, arriba. | La constante **`ARCHIVO = "configuracion.properties"`**. | «Y las credenciales no están escritas en el código: se leen de un archivo de configuración. Para pasar a otro servidor se cambia un archivo de texto, no se recompila.» |

### 3.5. Pestaña 4 — `LoginServlet.java`, **GET frente a POST**

| Pantalla | Señalo | Digo |
|---|---|---|
| Pestaña `LoginServlet.java`, **línea 49**. | La firma de **`doGet`**. | «Aquí se ve la diferencia entre los dos métodos HTTP que pide la evidencia. `doGet` **muestra** el formulario: es una consulta, no cambia nada y se puede repetir sin consecuencias.» |
| Bajar a la **línea 64**. | La firma de **`doPost`**. | «Y `doPost` lo **procesa**: valida las credenciales y crea la sesión. El login tiene que ser POST. Con GET, la contraseña quedaría escrita en la barra de direcciones, en el historial del navegador y en los registros del servidor.» |
| **Línea 79**. | El comentario del mensaje único. | «Cuando las credenciales fallan devuelvo el mismo mensaje para "no existe" y para "clave incorrecta". Si fueran distintos, cualquiera podría averiguar qué usuarios hay probando nombres.» |
| **Línea 86**. | El comentario de la sesión nueva. | «Y después de autenticar creo una sesión nueva, lo que evita la fijación de sesión.» |
| **Líneas 99 a 102**. | El comentario y el **`sendRedirect`**. | «Por último respondo con una redirección y no con un `forward`. Es el patrón POST-Redirect-GET: si respondiera directo al POST, al pulsar F5 el navegador reenviaría el formulario con la contraseña.» |

### 3.6. Pestaña 5 — `CotizacionServlet.java`, **la regla central**

| Pantalla | Señalo | Digo |
|---|---|---|
| Pestaña `CotizacionServlet.java`, **líneas 165 y 166**. | Las dos líneas `getParameterValues("productoId")` y `getParameterValues("cantidad")`. | «Este es el formulario más interesante del módulo, porque envía varias líneas a la vez. Cuando un formulario tiene varios campos con el mismo nombre, el servlet los recibe como arreglos, y la posición de uno corresponde a la del otro.» |
| Bajar a la **línea 199**. | La cabecera del método **`armarLineas`**. | «Aquí recorro esas líneas. De cada una valido que el producto exista, que esté activo y que la cantidad no supere las existencias.» |
| **Líneas 230 a 232**. | El comentario «El precio sale del objeto Producto» y la línea `new DetalleCotizacion(producto, cantidad)`. | «Y esta es la regla más importante de todo el módulo. Del formulario tomo **qué** producto y **cuántas** unidades. El **precio** lo busco yo en el catálogo. Si aceptara el precio que manda el navegador, cualquiera cotizaría un sofá en mil pesos editando el HTML antes de enviarlo. Esto lo voy a probar en vivo.» |
| Bajar a la **línea 279**. | El método **`esTransicionValida`**. | «Y aquí están declaradas las transiciones válidas del documento. Sin esto, una petición cualquiera podría devolver a borrador una cotización ya aprobada.» |

### 3.7. Pestaña 6 — `detalle.jsp`, **los elementos de JSP**

| Pantalla | Señalo | Digo |
|---|---|---|
| Pestaña `detalle.jsp`, **líneas 16 y 17**. | Las dos directivas **`<%@ taglib %>`**. | «Las vistas están en JSP. Arriba se declaran las librerías de etiquetas: `c` para la lógica de presentación y `fmt` para los formatos.» |
| **Línea 68**. | La etiqueta **`<c:forEach var="detalle">`**. | «`c:forEach` recorre la lista que dejó el servlet. La vista no consulta la base de datos: solo muestra lo que ya viene calculado.» |
| **Línea 104**. | La etiqueta **`<fmt:formatNumber type="currency">`**. | «`fmt:formatNumber` da formato de moneda en el servidor, sin construir la cadena a mano.» |
| **Línea 40**. | La etiqueta **`<c:out value="${cotizacion.clienteNombre}">`**. | «Y los datos que escribió el usuario los imprimo con `c:out`, que escapa los caracteres de HTML. Una expresión suelta como `${...}` no escapa nada: un nombre con una comilla doble se saldría del atributo de un formulario.» |

### 3.8. Pestaña 7 — `web.xml`, **el mapa del módulo**

| Pantalla | Señalo | Digo |
|---|---|---|
| Pestaña `web.xml`, **línea 84**. | Un bloque **`<servlet-mapping>`** completo y, en la **línea 117**, `<url-pattern>/clientes</url-pattern>`. | «Este es el descriptor de despliegue: aquí se declara qué clase atiende qué dirección. Uso `web.xml` en vez de anotaciones para que todo el mapa del módulo se lea en un solo archivo.» |
| **Líneas 62 y 71**. | Los dos bloques **`<filter-mapping>`**. | «Dos filtros, y el orden importa: primero se fija la codificación UTF-8, antes de que cualquier servlet lea un parámetro; después se verifica la sesión.» |
| **Línea 44**. | El `<param-name>` de la **locale de JSTL**. | «Esta línea vale la pena: declara la configuración regional de los formatos. Sin ella JSTL no lanza error, simplemente devuelve el número crudo y los montos aparecen sin formato de moneda. Fue uno de los defectos que encontraron las pruebas.» |
| Bajar al final del archivo. | Los bloques **`<error-page>`**. | «Y las páginas de error son propias, para que el usuario no vea nunca la traza de una excepción, que revelaría rutas de archivos y nombres de tablas.» |
| Volver al navegador. | La barra de direcciones, en una URL como `/clientes?accion=editar&id=1`. | «Fíjense en la forma de las direcciones: un servlet y un parámetro `accion`. Es el patrón que vimos en la formación, solo que aquí cada operación además verifica la sesión y el rol antes de ejecutarse.» |

---

## Bloque 4 — Pruebas en vivo (4:30 – 8:00)

**Nueve pruebas. Esta es la parte que demuestra que el módulo funciona.**

### Prueba 1 — El módulo exige sesión

| Pantalla | Señalo | Digo |
|---|---|---|
| Navegador. Escribir `localhost:8080/showroom/tablero` y Enter. | La barra de direcciones mientras se escribe. | «Voy a intentar entrar directamente al tablero, sin haber iniciado sesión.» |
| Aparece el login con el aviso. | El aviso **«Debe iniciar sesión para entrar al módulo»**. | «El filtro me devolvió al login. No se puede saltar la autenticación escribiendo la dirección.» |

### Prueba 2 — Contraseña incorrecta

| Pantalla | Señalo | Digo |
|---|---|---|
| Login con `admin` y una clave falsa escrita. | El campo de contraseña, antes de enviar. | «Pruebo con una contraseña equivocada de un usuario que sí existe.» |
| La respuesta con el error. | El texto **«Usuario o contraseña incorrectos»**. | «Me rechaza. Y fíjense: el mensaje no dice cuál de los dos está mal. Es a propósito: si dijera que el usuario no existe, cualquiera podría averiguar qué usuarios hay probando nombres.» |

### Prueba 3 — Ingreso correcto y tablero

| Pantalla | Señalo | Digo |
|---|---|---|
| Tablero recién cargado. | Los cuatro indicadores, de izquierda a derecha. | «Con las credenciales correctas entra al tablero: clientes activos, productos, productos por reponer y cotizaciones.» |
| El mismo. | El nombre y el rol, **arriba a la derecha**. | «Arriba aparece mi nombre y mi rol, administrador. Eso lo lee la vista de la sesión.» |
| Bajar a la tabla de rutas. | La columna **Métodos**, con las etiquetas GET y POST. | «Y dejé en el tablero la tabla de rutas del módulo, con los métodos que acepta cada una.» |

### Prueba 4 — Registrar un cliente

| Pantalla | Señalo | Digo |
|---|---|---|
| Menú **Clientes**, listado con 3 registros. | El conteo del subtítulo y las filas. | «Aquí están los tres clientes de ejemplo.» |
| Escribir `rojas` en el buscador y Buscar. | **La barra de direcciones**, que ahora muestra `?busqueda=rojas`. | «El buscador va por GET, y por eso la búsqueda queda en la dirección: se puede guardar en favoritos o compartir. Filtró correctamente.» |
| **Nuevo cliente** → enviar el formulario vacío. | Los mensajes de error, uno por uno. | «Envío el formulario vacío: me marca todos los campos obligatorios de una vez, no de uno en uno. Y esta validación ocurre **en el servidor**: los atributos del HTML ayudan al usuario, pero se desactivan desde el navegador, así que nunca son la última defensa.» |
| Llenar con los datos del **Anexo A** y enviar. | El aviso verde y la **fila nueva** en la tabla. | «Ahora con datos correctos. Creado. Esto fue un POST, y el servidor respondió con una redirección: por eso la dirección quedó limpia y puedo recargar sin duplicar el cliente.» |
| **Nuevo cliente** → documento `1032456789` → enviar. | El mensaje **«Ya existe otro cliente con ese documento»**. | «Y si repito el documento de un cliente que ya existe, lo rechaza: la regla de unicidad también se verifica en el servidor.» |

### Prueba 5 — El catálogo

| Pantalla | Señalo | Digo |
|---|---|---|
| Menú **Catálogo**, los 8 productos. | La columna **Stock** y, en particular, el *Escritorio ejecutivo* resaltado en naranja. | «Ocho productos. Este aparece resaltado porque está en su nivel mínimo: el sistema avisa qué hay que reponer.» |
| Filtrar por categoría **Salas**. | El selector de categoría y después la barra de direcciones. | «Los filtros también van por GET y se combinan: texto y categoría.» |

### Prueba 6 — Cotización con varias líneas y cálculo del IVA

| Pantalla | Señalo | Digo |
|---|---|---|
| **Cotizaciones → Nueva cotización**. | El selector de cliente y luego la columna **Cantidad**. | «El formulario muestra el catálogo completo y yo escribo cantidades solo en lo que quiero cotizar.» |
| Cliente *María Fernanda Rojas*; **2** en Sofá modular y **4** en Silla de comedor. | Cada casilla mientras se escribe el número. | «Dos sofás y cuatro sillas. Un formulario que envía varias líneas a la vez.» |
| Antes de enviar. | La columna **Precio unitario** de esas dos filas. | «Hagamos la cuenta: dos sofás de 2.890.000 son 5.780.000. Cuatro sillas de 185.000 son 740.000. Subtotal 6.520.000. El IVA del 19 % es 1.238.800. Total 7.758.800.» |
| Enviar. Detalle de la cotización. | Las tres líneas del pie de la tabla: **Subtotal, IVA, Total**. | «Subtotal 6.520.000, IVA 1.238.800, total 7.758.800. Exacto, y lo calculó el servidor.» |
| El mismo detalle. | El número **COT-000001** en el título. | «Y el número del documento lo asignó el servidor, consecutivo.» |

### Prueba 7 — La máquina de estados

| Pantalla | Señalo | Digo |
|---|---|---|
| Detalle en estado **borrador**. | La etiqueta de estado y el **único botón** disponible. | «La cotización nace en borrador, y el único paso posible es enviarla al cliente. No hay más botones, porque las transiciones válidas están declaradas en el código.» |
| Clic en **Enviar al cliente**. | Los **dos botones** que ahora aparecen. | «Ahora está enviada, y aparecen dos opciones: aprobada o rechazada.» |
| Clic en **Marcar como aprobada**. | La etiqueta de estado y **el espacio donde ya no hay botones**. | «La apruebo. Aprobada es un estado final: ya no hay botones para cambiarla.» |
| Ir al **tablero**. | El indicador **Monto aprobado**. | «Y el monto aprobado ya aparece en el tablero.» |

### Prueba 8 — Stock insuficiente

| Pantalla | Señalo | Digo |
|---|---|---|
| Nueva cotización, **99** unidades de *Escritorio ejecutivo*. | La columna **Disponible**, que dice 3. | «Pido 99 unidades de un producto del que solo hay 3.» |
| Enviar. | El mensaje **«Solo hay 3 unidades de Escritorio ejecutivo»**. | «Me dice exactamente cuántas hay, y no guardó nada: el documento no se crea a medias.» |

### Prueba 9a — El precio lo pone el servidor

| Pantalla | Señalo | Digo |
|---|---|---|
| Nueva cotización. Clic derecho en la casilla de cantidad del *Sofá modular* → **Inspeccionar**. | En el inspector, el `<input name="productoId">` de esa fila. | «Esta es la prueba que más me interesa. Voy a hacer de cuenta que soy un usuario malintencionado y voy a modificar el formulario desde el navegador.» |
| En el inspector: *Edit as HTML* y agregar `<input name="precio_unitario" value="1">`. | El campo recién agregado, con el valor **1**. | «Le agrego al formulario un campo de precio con valor de un peso, como si intentara llevarme el sofá por mil pesos.» |
| Cantidad 1, elegir cliente y enviar. | El precio unitario en el detalle: **2.890.000**. | «Y el precio que guardó es el del catálogo. Ignoró por completo el que yo mandé, porque el servidor nunca confía en el precio que viene del navegador: lo busca él mismo en la base de datos.» |

### Prueba 9b — Inyección SQL

| Pantalla | Señalo | Digo |
|---|---|---|
| **Clientes**, con `' OR 1=1 --` escrito en el buscador. | El texto dentro del campo de búsqueda. | «Ahora una carga de inyección SQL. Si la consulta estuviera mal hecha, esto devolvería todos los registros o rompería la aplicación.» |
| Enviar. Resultado vacío. | El mensaje de **ningún resultado**. | «Cero resultados y ningún error: buscó, literalmente, un cliente que se llame así. El texto se trató como dato, no como código.» |
| phpMyAdmin (`localhost:8081`) → tabla `clientes` → *Examinar*. | El **conteo de filas**, intacto. | «Y la tabla está intacta.» |

### Prueba 9c — Control por rol

| Pantalla | Señalo | Digo |
|---|---|---|
| **Salir** e ingresar con `asesor` / `Asesor2025`. | El rol **asesor**, arriba a la derecha. | «Cierro sesión y entro con el otro rol.» |
| Menú **Catálogo**. | **El espacio vacío** arriba a la derecha, donde antes estaba *Nuevo producto*. | «El asesor ve el catálogo, pero no tiene el botón de crear productos.» |
| Escribir `localhost:8080/showroom/productos?accion=nuevo`. | La barra de direcciones mientras se escribe. | «Pero ocultar un botón no es seguridad. Voy a escribir la dirección directamente, saltándome la interfaz.» |
| Página 403. | El número **403** y el título **Acceso denegado**. | «**403, acceso denegado.** El servlet verifica el rol en el servidor, no en la vista. Y es 403 y no 401: 401 significa "no sé quién eres"; 403 significa "sé quién eres, pero no te alcanza el permiso".» |
| Menús **Clientes** y **Cotizaciones**. | Los dos menús al entrar. | «El asesor sí puede gestionar clientes y cotizar, que es su trabajo.» |

---

## Bloque 5 — Versionamiento (8:00 – 9:00)

| Pantalla | Señalo | Digo |
|---|---|---|
| Terminal en la carpeta del proyecto, tras `git log --oneline`. | La lista completa, de arriba abajo. | «El proyecto está versionado con Git, con el historial organizado por capas: primero la infraestructura, luego los datos, la lógica, las vistas y la configuración.» |
| Ejecutar `git show 25da137`. | El **cuerpo** del mensaje, no la cabecera. | «Y los mensajes no dicen solo qué cambié, dicen **por qué**. Este explica por qué el login va por POST y por qué después de guardar hago una redirección.» |
| Navegador en el repositorio de GitHub. | La lista de archivos y el contador de commits. | «Está publicado en GitHub, en un repositorio público.» |
| **Ventana de incógnito** (`Ctrl+Shift+N`) con el mismo enlace. | La esquina del navegador, **sin sesión iniciada**. | «Y lo compruebo en incógnito, sin mi sesión: así es como lo va a ver el instructor. Se abre sin pedir credenciales.» |

---

## Bloque 6 — Cierre (9:00 – 9:40)

| Pantalla | Señalo | Digo |
|---|---|---|
| VS Code con `docs/02-pruebas-realizadas.md`. | La tabla del **resultado global**: 43 verificaciones. | «El módulo no solo está codificado, está probado: 43 verificaciones ejecutadas sobre la aplicación desplegada, con su resultado.» |
| El mismo archivo, sección de defectos. | Los títulos **DEF-01, DEF-02 y DEF-03**. | «Y los tres defectos que esas pruebas encontraron, con su causa y su corrección. Dos vale la pena mencionarlos: los montos se mostraban sin formato porque JSTL, cuando no resuelve la configuración regional, no lanza error sino que devuelve el número crudo. Y las vistas no escapaban los datos del usuario: un nombre con una comilla doble rompía el formulario.» |
| Volver al **tablero** del módulo. | Nada. Cursor quieto. | «Eso es el Módulo Comercial del Showroom: formularios HTML atendidos por servlets, con GET y POST, vistas en JSP, acceso a datos con consultas preparadas, control de acceso por rol y versionamiento en Git. Muchas gracias.» |

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

**Cotización (Prueba 6):** cliente *María Fernanda Rojas*, `2` sofás modulares,
`4` sillas de comedor, observación `Entrega en 8 dias`.

**Los números que hay que decir sin equivocarse:**

```
  2 x 2.890.000  =  5.780.000
  4 x   185.000  =    740.000
  Subtotal       =  6.520.000
  IVA 19 %       =  1.238.800
  TOTAL          =  7.758.800
```

**Carga de inyección SQL (Prueba 9b):** `' OR 1=1 --`

**Credenciales:** `admin` / `Admin2025` (administrador) · `asesor` / `Asesor2025` (asesor)

---

## Anexo B — Las 7 pestañas del bloque 3, en orden

| # | Pestaña | Líneas que se señalan |
|---|---|---|
| 1 | `Cliente.java` | 15 y siguientes · 93 |
| 2 | `ClienteDAO.java` | 41 · 45 · 23-26 · 151 · 157 |
| 3 | `ConexionBD.java` | 30 · 48 |
| 4 | `LoginServlet.java` | 49 · 64 · 79 · 86 · 99-102 |
| 5 | `CotizacionServlet.java` | 165-166 · 199 · 230-232 · 279 |
| 6 | `detalle.jsp` | 16-17 · 68 · 104 · 40 |
| 7 | `web.xml` | 84 · 117 · 62 · 71 · 44 |

> Si al abrir el archivo la línea no coincide exactamente, está a pocas líneas:
> use `Ctrl+G` y escriba el número, o `Ctrl+F` y busque el texto que aparece en
> la columna **Señalo**.

---

## Anexo C — Si algo falla durante la grabación

| Problema | Qué hacer |
|---|---|
| El navegador no conecta | Tomcat no está arriba. Ejecutar `iniciar-servidor.bat` y esperar 15 segundos. |
| Error diciendo que no hay conexión con la base | MySQL apagado. Iniciarlo en el panel de XAMPP. |
| Los montos salen como `6520000.00` | Se desplegó una versión vieja: ejecutar `compilar.bat desplegar`. |
| Tomcat no arranca: `Unable to establish loopback connection` | Verificar que `conf\server.xml` tenga `protocol="org.apache.coyote.http11.Http11Nio2Protocol"` en el conector del 8080. |
| El puerto 8080 está ocupado | Cerrar Docker Desktop, o cambiar el puerto en `server.xml`. |
| phpMyAdmin no abre | Apache apagado. `iniciar-servidor.bat` lo levanta; también se inicia desde el panel de XAMPP. Recuerde que es el puerto **8081**. |
| Quiero volver a empezar con datos limpios | Importar de nuevo `database/esquema.sql` en phpMyAdmin (`localhost:8081/phpmyadmin`). |

---

## Anexo D — Qué NO hacer

- **No leer el código línea por línea.** Señalar, explicar la decisión, seguir.
- **No mover el cursor mientras se habla.** Llevarlo al punto, dejarlo quieto, hablar.
- **No dejar silencios** mientras carga algo: es el momento de explicar qué se va a ver.
- **No improvisar los números** de la cotización. Están en el Anexo A.
- **No decir «aquí hice esto»** sin explicar por qué. La sustentación se evalúa por el criterio, no por el clic.
