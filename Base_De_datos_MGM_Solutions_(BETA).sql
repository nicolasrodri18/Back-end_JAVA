create database MGM_Solucions;

use MGM_Solucions;

-- 1. TBL_CIUDADES
CREATE TABLE TBL_CIUDADES (
    ID_Ciudad INT unique PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

-- 2. TBL_ROL
CREATE TABLE TBL_ROL (
    ID_ROL INT unique PRIMARY KEY,
    Nombre_ROL VARCHAR(50) NOT NULL
);

-- 3. TBL_USUARIOS
CREATE TABLE TBL_USUARIOS (
    DOCUMENTO_NIT VARCHAR(20) unique PRIMARY KEY,
    ID_ROL INT NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Direccion VARCHAR(255),
    Contraseña VARCHAR(255) NOT NULL,
    Ciudad INT NOT NULL,
    FOREIGN KEY (ID_ROL) REFERENCES TBL_ROL(ID_ROL),
    FOREIGN KEY (Ciudad) REFERENCES TBL_CIUDADES(ID_Ciudad)
);

-- 4. TBL_Permisos (Entidad de permisos)
CREATE TABLE TBL_Permisos (
    ID_Permiso INT PRIMARY KEY,
    Descripcion_Permiso VARCHAR(255) NOT NULL
);

-- 5. TBL_ROL_Permisos (Tabla de relación muchos a muchos entre ROL y Permisos)
CREATE TABLE TBL_ROL_Permisos (
    ID_Permiso INT unique NOT NULL,
    ID_ROL INT NOT NULL,
    FOREIGN KEY (ID_Permiso) REFERENCES TBL_Permisos(ID_Permiso),
    FOREIGN KEY (ID_ROL) REFERENCES TBL_ROL(ID_ROL)
);

-- 6. TBL_Correos
CREATE TABLE TBL_Correos (
    Correo VARCHAR(255) unique PRIMARY KEY,
    DOCUMENTO_NIT VARCHAR(20) NOT NULL,
    FOREIGN KEY (DOCUMENTO_NIT) REFERENCES TBL_USUARIOS(DOCUMENTO_NIT)
);

-- 7. TBL_Producto
CREATE TABLE TBL_Producto (
    ID_Producto INT unique PRIMARY KEY,
    Nombre_Producto VARCHAR(100) NOT NULL
);

-- 8. TBL_Almacen
CREATE TABLE TBL_Almacen (
    Registro_Almacen INT auto_increment unique PRIMARY KEY, -- Se asume un ID de registro de inventario/almacenamiento
    DOCUMENTO_NIT VARCHAR(20) NOT NULL,
    ID_Producto INT NOT NULL,
    Precio_Asignado DECIMAL(10, 2) NOT NULL,
    Precio_de_Compra DECIMAL(10, 2),
    Porcentaje_de_ganancia DECIMAL(5, 2),
    Stock INT NOT NULL,
    Fecha_Vencimiento DATE,
    FOREIGN KEY (DOCUMENTO_NIT) REFERENCES TBL_USUARIOS(DOCUMENTO_NIT),
    FOREIGN KEY (ID_Producto) REFERENCES TBL_Producto(ID_Producto)
);

-- 9. TBL_Venta
CREATE TABLE TBL_Venta (
    ID_Registro_Venta INT auto_increment unique PRIMARY KEY,
    DOCUMENTO_NIT VARCHAR(20) NOT NULL,
    Fecha DATE NOT NULL,
    Total_Venta DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (DOCUMENTO_NIT) REFERENCES TBL_USUARIOS(DOCUMENTO_NIT)
);

-- 10. TBL_Productos_Vendidos
CREATE TABLE TBL_Productos_Vendidos (
    ID_Venta INT auto_increment unique PRIMARY KEY,
    ID_Registro_Venta INT NOT NULL,
    Registro_Almacen INT NOT NULL,
    Cantidad_Vendida INT NOT NULL,
    FOREIGN KEY (ID_Registro_Venta) REFERENCES TBL_Venta(ID_Registro_Venta),
    FOREIGN KEY (Registro_Almacen) REFERENCES TBL_Almacen(Registro_Almacen)
);