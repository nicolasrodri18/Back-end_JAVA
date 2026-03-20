# Documentación Técnica: MGM Solutions

MGM Solutions es una plataforma de gestión empresarial diseñada para el control de inventarios, ventas y relaciones laborales entre empresas y empleados.

## 1. Arquitectura del Proyecto

El proyecto sigue el patrón **MVC (Model-View-Controller)**:

-   **Modelo (Model)**: Clases Java en `com.mgm_solutions.models` que representan las entidades (Ej: `Ciudad`).
-   **Vista (View)**: Archivos JSP en `src/main/webapp/JSPS` que generan la interfaz de usuario.
-   **Controlador (Controller)**: Servlets en `com.mgm_solutions.controllers` que gestionan la lógica de negocio y las peticiones HTTP.

---

## 2. Modelo de Datos (Base de Datos)

El sistema utiliza MySQL con el nombre `mgm_solucions`. Las tablas principales son:

### Usuarios y Roles
-   **`TBL_ROL`**: Define los niveles de acceso (Administrador, Empresa, Usuario).
-   **`TBL_USUARIOS`**: Almacena los datos de perfil y credenciales (bcrypt).
-   **`TBL_Correos`**: Tabla para gestionar los emails de contacto de los usuarios.
-   **`TBL_RELACION_LABORAL`**: Vincula un empleado (Usuario) con una Empresa y gestiona su estado (Activo/Inactivo).

### Inventario y Productos
-   **`TBL_Producto`**: Maestro de nombres de productos.
-   **`TBL_Almacen`**: El inventario real por empresa. Incluye stock, precio de compra, precio de venta, ganancia (%) y fecha de vencimiento.

### Ventas
-   **`TBL_Venta`**: Cabecera de la venta (quién vendió, fecha y total).
-   **`TBL_Productos_Vendidos`**: Detalle de los productos incluidos en cada venta.

---

## 3. Componentes del Backend (Java)

### Paquete `config`
-   **`ConnectionDB.java`**: Gestiona la conexión única a la base de datos MySQL.
-   **`SecurityUtils.java`**: Utilidades para el cifrado y validación de contraseñas (BCrypt).

### Paquete `controllers` (Servlets)
-   **`LoginServlet`**: Gestiona la autenticación y creación de sesiones.
-   **`RegistroEmpresaServlet` / `RegistroUsuarioServlet`**: Procesan la creación de nuevas cuentas.
-   **`InventarioServlet`**: CRUD de productos. Gestiona el stock y validaciones de vencimiento.
-   **`VentasServlet`**: Procesa las transacciones de venta, actualizando el stock y registrando el historial.
-   **`RelacionLaboralServlet`**: Gestiona las vinculaciones entre empresas y empleados.

---

## 4. Frontend y Lógica de Interfaz

### Estructura JSP
-   Organizada por carpetas según el rol: `Admin`, `Empleado`, `Empresa`.
-   Uso de Modales para acciones rápidas (Agregar producto, Editar perfil).

### JavaScript (`JS/`)
-   **`custom_alert.js`**: Sistema global de alertas que reemplaza al `alert()` nativo, devolviendo Promesas para sincronizar acciones (como recargas de página).
-   **`perfil-empresa.js` / `perfil-empleado.js`**: Gestionan las peticiones AJAX (`fetch`) y la manipulación dinámica del DOM de cada vista.
-   **`Script.js`**: Lógica general de navegación y efectos visuales de la landing page.

### Estilos (CSS)
-   Uso de variables CSS para un diseño consistente.
-   Diseño responsive con `flexbox` y `grid`.

---

## 5. Flujos Principales
1.  **Venta de Producto**: El empleado selecciona el producto -> Confirma la venta -> El `VentasServlet` descuenta el stock y registra la venta -> Se muestra alerta personalizada -> La página se refresca.
2.  **Gestión de Inventario**: La empresa añade productos con validación de ID (9 dígitos) y fecha de vencimiento -> Los datos se guardan en `TBL_Almacen`.

---

## 6. Estandarización de Idioma y Comentarios
Todo el proyecto ha sido estandarizado al **Español** para mejorar la legibilidad del equipo de desarrollo local.
-   **Comentarios**: Uso de Javadoc para Java y JSDoc para JavaScript.
-   **Logica**: Comentarios técnicos cortos en cada bloque de código crítico (validaciones, transacciones SQL, manejadores de eventos).
