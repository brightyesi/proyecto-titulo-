# Sistema de Gestión de Facturas Agrícolas — Backend

Plataforma web para automatizar la gestión de facturas de una empresa agrícola:
registro de documentos, control de estados, alertas automáticas de vencimiento
y gestión de usuarios por rol.

Este repositorio contiene el **backend** (API REST). El frontend (React) se
desarrolla por separado.

---

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Base de datos | PostgreSQL |
| Seguridad | Spring Security + JWT (jjwt 0.12.5) |
| Persistencia | Spring Data JPA / Hibernate |
| Correo | Spring Mail (Gmail SMTP) |
| Utilidades | Lombok |
| Build | Maven |

---

## Funcionalidades

- **Autenticación JWT** — login con correo y contraseña, sesiones sin estado (stateless).
- **Gestión de usuarios por ADMIN** — crear, listar y desactivar usuarios.
- **Facturas** — registrar, editar, listar, buscar por estado/vencimiento.
- **Papelera (soft delete)** — las facturas eliminadas se conservan 30 días y se pueden restaurar; pasado ese plazo se borran automáticamente.
- **Historial de estados** — registra cada cambio de estado de una factura.
- **Documentos** — subir y visualizar el PDF/imagen de cada factura (almacenamiento local).
- **Motor de alertas** — correo automático cuando una factura PENDIENTE vence en 5, 3 o 0 días (todos los días a las 08:00).
- **Resumen semanal** — correo a los administradores con las facturas que vencen la próxima semana (lunes a las 09:00).
- **Manejo global de errores** — respuestas de error consistentes vía `@RestControllerAdvice`.

---

## Roles y permisos

| Acción | EJECUTIVO | ADMINISTRADOR | ADMIN |
|---|:---:|:---:|:---:|
| Ver facturas / buscar / historial / documento | ✅ | ✅ | ✅ |
| Crear / editar facturas | ❌ | ✅ | ✅ |
| Eliminar / restaurar / papelera | ❌ | ✅ | ✅ |
| Subir documentos | ❌ | ✅ | ✅ |
| Gestionar proveedores | ❌ | ✅ | ✅ |
| Gestionar usuarios | ❌ | ❌ | ✅ |

---

## Requisitos previos

- Java 21
- Maven
- PostgreSQL en ejecución

---

## Configuración

### 1. Crear la base de datos

```sql
CREATE DATABASE agricola_db;
```

### 2. Configurar `src/main/resources/application.properties`

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/agricola_db
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD

# Correo (completar cuando se disponga del correo de la empresa)
spring.mail.username=CORREO@gmail.com
spring.mail.password=APP_PASSWORD_DE_GMAIL
```

> ⚠️ Las credenciales de correo usan una **Contraseña de Aplicación** de Gmail, no la contraseña normal de la cuenta.

### 3. Ejecutar

```bash
./mvnw spring-boot:run
```

> En Windows (PowerShell): `.\mvnw.cmd spring-boot:run`

La API queda disponible en `http://localhost:8081`.

Al primer arranque, se crean automáticamente los roles
(`ROLE_ADMIN`, `ROLE_ADMINISTRADOR`, `ROLE_EJECUTIVO`) y las configuraciones
de alerta (5, 3 y 0 días).

---

## Endpoints principales

### Autenticación
| Método | Ruta | Acceso |
|---|---|---|
| POST | `/user/login` | Público |

### Usuarios (solo ADMIN)
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/admin/usuarios` | Crear usuario |
| GET | `/admin/usuarios` | Listar usuarios |
| DELETE | `/admin/usuarios/{id}` | Desactivar usuario |

### Facturas
| Método | Ruta | Acceso |
|---|---|---|
| GET | `/facturas` | Autenticado |
| GET | `/facturas/{id}` | Autenticado |
| GET | `/facturas/busqueda` | Autenticado |
| GET | `/facturas/{id}/historial` | Autenticado |
| GET | `/facturas/{id}/documento` | Autenticado |
| POST | `/facturas` | ADMINISTRADOR, ADMIN |
| PUT | `/facturas/{id}` | ADMINISTRADOR, ADMIN |
| DELETE | `/facturas/{id}` | ADMINISTRADOR, ADMIN |
| PUT | `/facturas/{id}/restaurar` | ADMINISTRADOR, ADMIN |
| GET | `/facturas/papelera` | ADMINISTRADOR, ADMIN |
| POST | `/facturas/{id}/documento` | ADMINISTRADOR, ADMIN |

### Proveedores
| Método | Ruta | Acceso |
|---|---|---|
| GET | `/proveedores` | Autenticado |
| GET | `/proveedores/{id}` | Autenticado |
| POST | `/proveedores` | ADMINISTRADOR, ADMIN |
| PUT | `/proveedores/{id}` | ADMINISTRADOR, ADMIN |
| DELETE | `/proveedores/{id}` | ADMINISTRADOR, ADMIN |

> Todas las rutas excepto `/user/login` requieren el header
> `Authorization: Bearer <token>`.

---

## Estructura del proyecto

```
src/main/java/Proceso_Administrativo/proyecto_titulo/
├── Config/        → Seguridad, correo, datos iniciales
├── Controller/    → Endpoints REST
├── DTO/           → Objetos de entrada/salida
├── Exception/     → Manejo global de errores
├── Modelo/        → Entidades JPA
├── Repository/    → Acceso a datos
├── Scheduler/     → Limpieza automática de papelera
├── Security/      → Filtro y servicio JWT
└── Service/       → Lógica de negocio
```

---

## Tareas programadas

| Tarea | Frecuencia | Descripción |
|---|---|---|
| Motor de alertas | Diaria 08:00 | Avisa facturas que vencen en 5/3/0 días |
| Resumen semanal | Lunes 09:00 | Resumen de vencimientos a administradores |
| Limpieza de papelera | Diaria 00:00 | Borra definitivo tras 30 días |

---

## Tests

El proyecto incluye pruebas unitarias de la capa de servicios (lógica de negocio)
con **JUnit 5** y **Mockito**, más una prueba de integración que valida el
arranque del contexto con **H2** (base de datos en memoria).

| Clase de prueba | Cubre |
|---|---|
| `AlertaServiceTest` | Motor de alertas (5/3/0 días, anti-duplicados) |
| `FacturaServiceTest` | Soft delete, restaurar, historial de estados |
| `ResumenSemanalServiceTest` | Resumen semanal de vencimientos |
| `UserServiceTest` | Login, creación y desactivación de usuarios |
| `ProveedorServiceTest` | Creación y desactivación de proveedores |

Los tests no requieren PostgreSQL: usan H2 en memoria mediante el perfil `test`.

Ejecutar todos los tests:

```bash
./mvnw test
```

> En Windows (PowerShell): `.\mvnw.cmd test`

---

## Equipo

Proyecto de título desarrollado en equipo.
Integrantes: Camila Malhue, Yesenia Jara y Cristian Tapia
