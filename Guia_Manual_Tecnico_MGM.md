# 📘 Guía para el Manual Técnico – MGM Solutions

> **Audiencia del manual:** Personal técnico (IT, administradores, desarrolladores, auditores).  
> **Norma:** APA. Tus fotos/capturas irán exactamente donde se indica con 📷.

---

## PORTADA (Norma APA)

**Qué poner:**

| Campo | Valor |
|---|---|
| Título | **Sistema de Gestión de Inventario y Ventas MGM Solutions** *(máx. 12 palabras, negrita, centrado)* |
| Autor | Tu nombre completo |
| Institución | SENA |
| Programa | Análisis y Desarrollo de Software – Ficha 2994281 |
| Instructor/Decano | Nombre de tu instructor |
| Ubicación | Ciudad, Colombia |
| Fecha | Marzo 2026 |

---

## ÍNDICE / TABLA DE CONTENIDO

Genera el índice al final, cuando el documento esté listo. Incluye las 12 secciones:

1. Introducción
2. Descripción General del Sistema
3. Requisitos Técnicos del Sistema
4. Instalación y Configuración
5. Arquitectura del Software y Base de Datos
6. Diseño del Software
7. Interfaces del Sistema
8. Manejo de Mensajes de Ayuda y de Errores
9. Conclusión(es)
10. Mantenimiento y Actualizaciones
11. Referencia Bibliográfica
12. Anexos

---

## 1. INTRODUCCIÓN

**Qué escribir:**
- **Propósito del manual:** Este documento describe la arquitectura, configuración e instalación del sistema web MGM Solutions, diseñado para la gestión de inventario, ventas y relaciones laborales entre empresas y empleados.
- **Alcance:** El sistema cubre tres módulos principales: gestión de usuarios/roles, control de inventario (almacén) y registro de ventas, accesibles por tres tipos de usuarios: Administrador, Empresa y Empleado.
- **Justificación:** Fue desarrollado como proyecto integrador del programa ADSO del SENA, para digitalizar y centralizar los procesos comerciales de pequeñas y medianas empresas.

---

## 2. DESCRIPCIÓN GENERAL DEL SISTEMA

**Qué escribir:**

### Objetivo General
Proporcionar una plataforma web que permita a empresas y sus empleados gestionar inventarios, registrar ventas en tiempo real y administrar relaciones laborales de manera centralizada y segura.

### Objetivos Específicos
- Permitir el registro y autenticación de usuarios con roles diferenciados.
- Gestionar el inventario de productos con control de stock y fechas de vencimiento.
- Registrar y consultar el historial de ventas por empresa y empleado.
- Administrar la relación laboral entre empresas y empleados (activo/inactivo).

### Requisitos Funcionales

| ID | Funcionalidad |
|---|---|
| RF-01 | El sistema permite iniciar sesión con documento/NIT y contraseña |
| RF-02 | El sistema redirige al usuario según su rol (Admin, Empresa, Empleado) |
| RF-03 | La empresa puede registrar empleados y gestionar su estado laboral |
| RF-04 | La empresa gestiona su inventario (agregar, editar, eliminar productos) |
| RF-05 | El empleado puede realizar ventas descontando stock automáticamente |
| RF-06 | El sistema valida vencimiento de productos antes de permitir la venta |
| RF-07 | Solo se pueden revertir ventas del día actual |
| RF-08 | El administrador puede gestionar empresas y empleados |

### Requisitos No Funcionales

| ID | Requisito |
|---|---|
| RNF-01 | Las contraseñas se almacenan con hash BCrypt (seguridad) |
| RNF-02 | La aplicación es responsiva (soporta tablet y portátil) |
| RNF-03 | El sistema opera sobre HTTP con sesiones Java (HttpSession) |
| RNF-04 | Las operaciones críticas (ventas) usan transacciones atómicas SQL |
| RNF-05 | Tiempo de respuesta inferior a 2 segundos en red local |

---

## 3. REQUISITOS TÉCNICOS DEL SISTEMA

**Qué escribir:**

### Hardware Mínimo

| Componente | Especificación Mínima |
|---|---|
| Procesador | Intel Core i3 o equivalente |
| RAM | 4 GB |
| Almacenamiento | 10 GB libres |
| Red | Conexión LAN o localhost |

### Hardware Recomendado

| Componente | Especificación |
|---|---|
| Procesador | Intel Core i5/i7 o equivalente |
| RAM | 8 GB o más |
| Almacenamiento | SSD con 20 GB libres |

### Software Requerido

| Software | Versión | Rol |
|---|---|---|
| Java JDK | 17 | Lenguaje de desarrollo |
| Apache Maven | 3.9.12 | Gestor de dependencias y build |
| Apache Tomcat | 10.x | Servidor de aplicaciones |
| MySQL Server | 8.x | Gestor de base de datos |
| MySQL Connector/J | 8.3.0 | Driver JDBC para conexión Java-MySQL |
| Jakarta Servlet API | 5.0.0 | API de servlets (Jakarta EE) |
| JSTL | 2.0.0 | JSP Standard Tag Library |
| BCrypt (jBCrypt) | 0.4 | Hashing seguro de contraseñas |
| IDE recomendado | NetBeans / IntelliJ IDEA / VS Code | Desarrollo |
| Sistema Operativo | Windows 10/11 (dev), Linux (producción) | — |

---

## 4. INSTALACIÓN Y CONFIGURACIÓN

**Qué escribir:**

### Tipo de Aplicación
Aplicación web Java EE desplegada como archivo `.war` en Apache Tomcat 10.

### Pasos de Instalación

#### 4.1 Configurar la Base de Datos MySQL
1. Instalar MySQL Server 8.x.
2. Crear usuario y base de datos:
```sql
CREATE DATABASE mgm_solucions;
CREATE USER 'mgm_solucions'@'localhost' IDENTIFIED BY 'mgm_solucions';
GRANT ALL PRIVILEGES ON mgm_solucions.* TO 'mgm_solucions'@'localhost';
FLUSH PRIVILEGES;
```
3. Ejecutar el script `Base_De_datos_MGM_Solutions.sql` para crear las tablas y datos iniciales.

#### 4.2 Configurar la Conexión JDBC
El archivo `ConnectionDB.java` define los parámetros de conexión:
```java
URL  = "jdbc:mysql://localhost:3306/mgm_solucions"
USER = "mgm_solucions"
PASS = "mgm_solucions"
Puerto MySQL: 3306 (por defecto)
```

#### 4.3 Compilar y Empaquetar el Proyecto
```bash
cd mgm_solutions/
mvn clean package
# Genera: target/mgm_solutions.war
```

#### 4.4 Desplegar en Apache Tomcat 10
1. Copiar `target/mgm_solutions.war` a la carpeta `webapps/` de Tomcat.
2. Iniciar Tomcat (bin/startup.bat en Windows).
3. Acceder en el navegador: `http://localhost:8080/mgm_solutions/`

#### 4.5 Puerto y Acceso
- **Puerto Tomcat:** 8080 (por defecto, configurable en `conf/server.xml`)
- **URL de inicio de sesión:** `http://localhost:8080/mgm_solutions/index.jsp`

📷 **FOTO AQUÍ:** Captura de pantalla de MySQL Workbench mostrando las tablas creadas y la pantalla de inicio del sistema en el navegador.

---

## 5. ARQUITECTURA DEL SOFTWARE Y BASE DE DATOS

**Qué escribir:**

### 5.1 Arquitectura de la Aplicación
El sistema sigue el patrón **MVC (Modelo - Vista - Controlador)**:

| Capa | Tecnología | Ubicación |
|---|---|---|
| Vista (View) | JSP + HTML + CSS + JS | `src/main/webapp/JSPS/` |
| Controlador (Controller) | Java Servlets (Jakarta) | `src/main/java/.../controllers/` |
| Modelo (Model) | Java + JDBC + MySQL | `src/main/java/.../config/` + DB |

### 5.2 Diagrama de Casos de Uso (UML)
> Crea o inserta aquí el diagrama. Actores: **Administrador**, **Empresa**, **Empleado**.

📷 **FOTO AQUÍ:** Diagrama de casos de uso (puedes dibujarlo en draw.io o Lucidchart e imprimirlo/capturarlo).

**Casos de uso por actor:**

| Actor | Casos de Uso |
|---|---|
| Administrador | Iniciar sesión, gestionar empresas, gestionar empleados, seleccionar rol |
| Empresa | Iniciar sesión, gestionar inventario, ver empleados, gestionar ventas, cambiar estado empleado |
| Empleado | Iniciar sesión, ver productos disponibles, registrar ventas, ver historial propio |

### 5.3 Estructura de la Base de Datos

**Base de datos:** `mgm_solucions`  
**11 tablas:**

| Tabla | Descripción |
|---|---|
| `TBL_CIUDADES` | Catálogo de ciudades de Colombia |
| `TBL_ROL` | Roles del sistema: Administrador, Empresa, Usuario |
| `TBL_USUARIOS` | Usuarios del sistema (empresas y empleados) |
| `TBL_Permisos` | Permisos disponibles en el sistema |
| `TBL_ROL_Permisos` | Relación muchos-a-muchos entre roles y permisos |
| `TBL_Correos` | Correos electrónicos asociados a usuarios |
| `TBL_Producto` | Catálogo maestro de productos |
| `TBL_Almacen` | Inventario físico de productos por empresa (stock, precio, vencimiento) |
| `TBL_Venta` | Registro de cabecera de ventas |
| `TBL_Productos_Vendidos` | Detalle de productos por venta |
| `TBL_RELACION_LABORAL` | Vínculo empleado-empresa con estado (Activo/Inactivo) |

📷 **FOTO AQUÍ:** Diagrama Entidad-Relación (ER) de la base de datos. Puedes generarlo en MySQL Workbench: *Database → Reverse Engineer*.

### 5.4 Script de Creación
Indica que el script completo se encuentra en el archivo `Base_De_datos_MGM_Solutions.sql` adjunto en los Anexos.

---

## 6. DISEÑO DEL SOFTWARE

**Qué escribir:**

### 6.1 Módulos del Sistema

#### Módulo de Autenticación (`LoginServlet.java`)
- Recibe documento/NIT y contraseña vía POST.
- Consulta `TBL_USUARIOS` por NIT y verifica contraseña con BCrypt.
- Configura la sesión HTTP con: `userDoc`, `userName`, `userRol`, `userEmail`, `ciaNit`, etc.
- Redirige según rol: Admin → `seleccion-rol.jsp`, Empresa → `inicio-empresa.jsp`, Empleado → `inicio-empleado.jsp`.
- Soporta peticiones AJAX (responde JSON) y formularios tradicionales.

#### Módulo de Registro (`RegistroEmpresaServlet.java` / `RegistroUsuarioServlet.java`)
- Permite el alta de nuevas empresas y empleados en el sistema.
- Registra datos en `TBL_USUARIOS` y `TBL_Correos`.

#### Módulo de Inventario (`InventarioServlet.java`)
- Gestiona el inventario de la empresa autenticada en `TBL_Almacen`.
- Operaciones: listar productos, agregar, editar stock/precio, eliminar.

#### Módulo de Ventas (`VentasServlet.java`)
- **Registrar venta:** Valida stock y vencimiento → inserta en `TBL_Venta` → inserta en `TBL_Productos_Vendidos` → descuenta stock en `TBL_Almacen`. Todo en transacción atómica (`conn.setAutoCommit(false)`).
- **Listar ventas:** Consulta historial de ventas de la empresa con detalle de empleado y producto.
- **Eliminar venta:** Solo para ventas del día actual; restaura el stock al revertir.

#### Módulo de Relación Laboral (`RelacionLaboralServlet.java`)
- Administra el vínculo empresa-empleado (`TBL_RELACION_LABORAL`).
- Permite cambiar el estado del empleado: Activo / Inactivo / Disponible.
- Un empleado inactivo no puede registrar ventas (validado en backend).

### 6.2 Flujo de Interacción de Componentes

```
Navegador (JSP) → [fetch/POST] → Servlet (Java) → [JDBC] → MySQL
                ← [JSON/redirect] ←             ←
```

📷 **FOTO AQUÍ:** Captura de pantalla del código del servlet `LoginServlet.java` abierto en el IDE (muestra la lógica de roles).

---

## 7. INTERFACES DEL SISTEMA

**Qué escribir:**

### 7.1 Vistas JSP disponibles

| Archivo JSP | Descripción | Rol que la usa |
|---|---|---|
| `index.jsp` | Página de bienvenida con enlace al login | Todos |
| `JSPS/Login.jsp` | Formulario de inicio de sesión | Todos |
| `JSPS/seleccion-rol.jsp` | Selección de panel según rol | Administrador |
| `JSPS/Empresa/inicio-empresa.jsp` | Panel principal de empresa | Empresa |
| `JSPS/Empresa/Datos-empresa.jsp` | Perfil y datos de la empresa | Empresa |
| `JSPS/Empresa/Lista-productos-empresa.jsp` | Gestión de inventario | Empresa |
| `JSPS/Empresa/Listas-empleados-empresa.jsp` | Lista y gestión de empleados | Empresa |
| `JSPS/Empresa/Listas-ventas-empresa.jsp` | Historial de ventas | Empresa |
| `JSPS/Empleado/inicio-empleado.jsp` | Panel principal del empleado | Empleado |
| `JSPS/Empleado/Datos-empleado.jsp` | Perfil del empleado | Empleado |
| `JSPS/Empleado/Lista-productos-empleado.jsp` | Lista de productos para vender | Empleado |

### 7.2 APIs / Endpoints (Servlets)

| Endpoint | Método | Acción | Parámetros clave |
|---|---|---|---|
| `/LoginServlet` | POST | Autenticar usuario | `nit-documento`, `password` |
| `/InventarioServlet` | GET | Listar inventario empresa | `action=listar` |
| `/InventarioServlet` | POST | Agregar/editar/eliminar producto | `action=agregar/editar/eliminar` |
| `/VentasServlet` | GET | Listar ventas empresa | `action=listarEmpresa` |
| `/VentasServlet` | POST | Registrar venta | `action=registrarVenta`, `idRegistroAlmacen`, `cantidad`, `total` |
| `/VentasServlet` | POST | Eliminar venta | `action=eliminar`, `id` |
| `/RelacionLaboralServlet` | GET/POST | Gestionar relación laboral | `action=listar/cambiarEstado` |
| `/RegistroEmpresaServlet` | POST | Registrar empresa | datos del formulario |
| `/RegistroUsuarioServlet` | POST | Registrar empleado | datos del formulario |

### 7.3 Autenticación y Autorización
- **Método:** Sesiones HTTP del servidor (`HttpSession`).
- **Variables de sesión clave:** `userDoc` (NIT), `userRol` (1=Admin, 2=Empresa, 3=Empleado), `userName`, `statusRelacion`.
- **Seguridad de contraseñas:** Hash BCrypt con `SecurityUtils.checkPassword()`.
- **Control de acceso:** Cada JSP/Servlet verifica la sesión; si no hay sesión válida, redirige al login.

📷 **FOTO AQUÍ:** Capturas de la interfaz de cada módulo principal (pantalla de login, panel empresa, lista de productos, registro de venta, lista de empleados).

---

## 8. MANEJO DE MENSAJES DE AYUDA Y DE ERRORES

**Qué escribir:**

### Mensajes de Error del Backend

| Código / Situación | Mensaje mostrado | Causa |
|---|---|---|
| `loginError=1` | Contraseña incorrecta | Contraseña no coincide con hash BCrypt |
| `loginError=2` | Usuario no encontrado | NIT no existe en la base de datos |
| `loginError=db` | Error de servidor | Falla de conexión a MySQL |
| Stock = 0 | "No hay stock disponible (VACÍO)" | El producto no tiene unidades |
| Stock insuficiente | "Stock insuficiente. Disponible: X" | Cantidad pedida > stock actual |
| Producto vencido | "No se puede vender un producto vencido" | `Fecha_Vencimiento` anterior a hoy |
| Empleado inactivo | "Tu cuenta está inactiva. No puedes realizar ventas." | `statusRelacion = 'Inactivo'` |
| Venta pasada | "Solo se pueden eliminar ventas del día actual" | Intento de eliminar venta de fecha anterior |
| HTTP 401 | Error de sesión no encontrada | Sesión expirada o no iniciada |
| HTTP 500 | Error interno del servidor | Excepción no controlada en el servidor |

### Mensajes de Éxito
- Inicio de sesión exitoso → redirige al panel correspondiente.
- Venta registrada → `{"status":"success", "message":"Venta registrada correctamente."}`.
- Venta eliminada → `{"status":"success", "message":"Venta eliminada y stock restaurado correctamente."}`.

### Alertas Personalizadas del Frontend
El sistema usa alertas personalizadas (`custom_alert.css`) en lugar de alerts nativos del navegador.

📷 **FOTO AQUÍ:** Captura de pantalla de un mensaje de error en acción (por ejemplo, intentar vender con stock 0 o ingresar contraseña incorrecta).

---

## 9. CONCLUSIONES

**Qué escribir (guía, redacta con tus palabras):**
- El sistema MGM Solutions logra digitalizar y centralizar los procesos de inventario, ventas y gestión de personal en una plataforma web funcional.
- La implementación del patrón MVC con Java Servlets y JSP facilitó la separación de responsabilidades y el mantenimiento del código.
- La integración de BCrypt garantiza la seguridad de las credenciales de los usuarios.
- El uso de transacciones atómicas en el módulo de ventas asegura la integridad de los datos ante posibles fallos.
- El proyecto representa una solución escalable que puede adaptarse a diferentes tipos de negocios.

---

## 10. MANTENIMIENTO Y ACTUALIZACIONES

**Qué escribir:**

### Control de Versiones
| Versión | Fecha | Cambios realizados | Responsable |
|---|---|---|---|
| 1.0-SNAPSHOT | Marzo 2026 | Versión inicial funcional | Nicolás Rodríguez |

### Recomendaciones de Mantenimiento
- **Base de datos:** Realizar backups semanales del esquema `mgm_solucions`.
- **Dependencias:** Revisar actualizaciones de `mysql-connector-j`, `jakarta.servlet-api` y `jbcrypt` semestralmente.
- **Contraseñas:** El factor de trabajo BCrypt puede aumentarse si se requiere mayor seguridad.
- **Escalabilidad:** Para producción, migrar de conexiones directas JDBC a un pool de conexiones (HikariCP o DBCP).
- **Tomcat:** Actualizar a la última versión estable de Tomcat 10.x para parches de seguridad.

---

## 11. REFERENCIA BIBLIOGRÁFICA (Norma APA)

**Qué incluir (ejemplos de formato APA 7ª edición):**

```
Apache Software Foundation. (2024). Apache Tomcat 10 Documentation. https://tomcat.apache.org/

Eclipse Foundation. (2024). Jakarta Servlet Specification 5.0. https://jakarta.ee/specifications/servlet/5.0/

jBCrypt. (2011). jBCrypt - OpenBSD-style Blowfish password hashing for Java. https://www.mindrot.org/projects/jBCrypt/

MySQL AB. (2024). MySQL 8.0 Reference Manual. Oracle Corporation. https://dev.mysql.com/doc/refman/8.0/en/

Oracle Corporation. (2024). Java SE 17 Documentation. https://docs.oracle.com/en/java/javase/17/

SENA. (2025). Guía de Proyecto Integrador – ADSO Ficha 2994281. Servicio Nacional de Aprendizaje.
```

---

## 12. ANEXOS

### Anexo A – Glosario de Términos

| Término | Definición |
|---|---|
| **BCrypt** | Algoritmo de hashing seguro para contraseñas, resistente a ataques de fuerza bruta |
| **DAO** | Data Access Object – patrón de acceso a datos en la capa de persistencia |
| **JDBC** | Java Database Connectivity – API estándar de Java para conexión a bases de datos |
| **JSP** | JavaServer Pages – tecnología para crear páginas web dinámicas con Java |
| **MVC** | Model-View-Controller – patrón arquitectónico de separación de responsabilidades |
| **NIT** | Número de Identificación Tributaria – identificador de empresas en Colombia |
| **Servlet** | Componente Java del servidor que maneja peticiones HTTP |
| **Sesión HTTP** | Mecanismo del servidor para mantener estado de usuario entre peticiones |
| **Stock** | Cantidad de unidades disponibles de un producto en el inventario |
| **Transacción atómica** | Conjunto de operaciones SQL que se ejecutan como una unidad indivisible |
| **WAR** | Web Application Archive – formato empaquetado de aplicaciones web Java |

### Anexo B – Contacto del Equipo Técnico

| Rol | Nombre | Contacto |
|---|---|---|
| Desarrollador Principal | Nicolás Rodríguez | [correo@ejemplo.com] |
| Instructor | [Nombre del instructor] | [correo institucional] |
| Institución | SENA – Centro de Comercio | [dirección/teléfono] |

### Anexo C – Script de Base de Datos
> Adjuntar el archivo `Base_De_datos_MGM_Solutions.sql` completo o insertarlo aquí.

---

## 📋 RESUMEN: ¿QUÉ FOTOS DEBES TOMAR?

| # | Sección | Qué capturar |
|---|---|---|
| 1 | Cap. 4 – Instalación | MySQL Workbench con las tablas creadas + pantalla de inicio en el navegador |
| 2 | Cap. 5 – Arquitectura | Diagrama ER (desde MySQL Workbench: Database → Reverse Engineer) |
| 3 | Cap. 5 – Casos de Uso | Diagrama UML de actores (draw.io) |
| 4 | Cap. 6 – Diseño | Código del LoginServlet en el IDE |
| 5 | Cap. 7 – Interfaces | Pantalla de Login |
| 6 | Cap. 7 – Interfaces | Panel de inicio Empresa |
| 7 | Cap. 7 – Interfaces | Lista de productos / Inventario |
| 8 | Cap. 7 – Interfaces | Pantalla de registro de venta |
| 9 | Cap. 7 – Interfaces | Lista de empleados con estado activo/inactivo |
| 10 | Cap. 7 – Interfaces | Historial de ventas |
| 11 | Cap. 8 – Errores | Mensaje de error en acción (contraseña incorrecta o stock 0) |
