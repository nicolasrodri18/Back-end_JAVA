
create database mgm_solucions;

use mgm_solucions;

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

-- 11. TBL_RELACION_LABORAL (Vínculo Empleado - Empresa)
CREATE TABLE TBL_RELACION_LABORAL (
    ID_Relacion INT auto_increment unique PRIMARY KEY,
    NIT_EMPLEADO VARCHAR(20) NOT NULL,
    NIT_EMPRESA VARCHAR(20) NOT NULL,
    Estado VARCHAR(20) DEFAULT 'Activo',
    FOREIGN KEY (NIT_EMPLEADO) REFERENCES TBL_USUARIOS(DOCUMENTO_NIT),
    FOREIGN KEY (NIT_EMPRESA) REFERENCES TBL_USUARIOS(DOCUMENTO_NIT)
);

INSERT INTO TBL_ROL (ID_ROL, Nombre_ROL) VALUES (1, 'Administrador');
INSERT INTO TBL_ROL (ID_ROL, Nombre_ROL) VALUES (2, 'Empresa');
INSERT INTO TBL_ROL (ID_ROL, Nombre_ROL) VALUES (3, 'Usuario');
INSERT INTO TBL_Permisos (ID_Permiso, Descripcion_Permiso) VALUES (1, 'Administrar todo');
INSERT INTO TBL_Permisos (ID_Permiso, Descripcion_Permiso) VALUES (2, 'Administrar usuarios');
INSERT INTO TBL_Permisos (ID_Permiso, Descripcion_Permiso) VALUES (3, 'Administrar productos');
INSERT INTO TBL_Permisos (ID_Permiso, Descripcion_Permiso) VALUES (4, 'Administrar ventas');
INSERT INTO TBL_ROL_Permisos (ID_Permiso, ID_ROL) VALUES (1, 1);
INSERT INTO TBL_ROL_Permisos (ID_Permiso, ID_ROL) VALUES (2, 1);
INSERT INTO TBL_ROL_Permisos (ID_Permiso, ID_ROL) VALUES (3, 1);
INSERT INTO TBL_ROL_Permisos (ID_Permiso, ID_ROL) VALUES (4, 1);

INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (1, 'Bogota');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (2, 'Medellin');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (3, 'Cali');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (4, 'Barranquilla');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (5, 'Cartagena');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (6, 'Bucaramanga');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (7, 'Manizales');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (8, 'Pereira');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (9, 'Barrancabermeja');
INSERT INTO TBL_CIUDADES (ID_Ciudad, Nombre) VALUES (10, 'Barrancabermeja');

INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (1097765432, 1, 'Administrador', 'Calle 123 # 45-67', '$2a$10$TeOHFsUohFDT9EIR86xTnefja7oEzBOyuaag2kawWA6rdHdAVJdPe', 1);
INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (1097765431, 2, 'Empresa', 'Cra 43 # 56-78', '$2a$10$J8Y8EDdieYa6V6nTV0afYeDkfPCv9g7RjB1L1h0sYIJMbWBQ4Hlo2', 2);
INSERT INTO TBL_USUARIOS (DOCUMENTO_NIT, ID_ROL, Nombre, Direccion, Contraseña, Ciudad) VALUES (1097765433, 3, 'Usuario', 'Calle 123 # 45-67', '$2a$10$RPD0AO3qoWvwxjy8DG/8fONS1WLNrFTaaf.evmZyX1pv59Q2g9JnG', 3);

INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES ('Administrador@gmail.com', 1097765432);
INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES ('Empresa@gmail.com', 1097765431);
INSERT INTO TBL_Correos (Correo, DOCUMENTO_NIT) VALUES ('Usuario@gmail.com', 1097765433);

select * from tbl_usuarios;
select * from tbl_correos;
select * from tbl_relacion_laboral;
select * from tbl_venta;
select * from tbl_productos_vendidos;
select * from tbl_almacen;
select * from tbl_producto;

delete from tbl_ciudades where ID_Ciudad = 10;

delete from tbl_usuarios;
delete from tbl_correos;
delete from tbl_relacion_laboral;
delete from tbl_venta;
delete from tbl_productos_vendidos;
delete from tbl_almacen;
delete from tbl_producto;
