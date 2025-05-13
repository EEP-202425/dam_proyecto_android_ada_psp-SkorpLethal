# Transportes App

Una aplicación completa para la gestión de marcas y modelos de vehículos desarrollada con un **Frontend en Kotlin para Android** y un **Backend en Spring Boot con API REST**. El objetivo es mostrar el uso completo de una arquitectura cliente-servidor moderna, con persistencia de datos en MySQL.

---

## Tabla de Contenidos

- [Tecnologías Usadas](#tecnologías-usadas)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Backend (Spring Boot)](#backend-spring-boot)
  - [Entidades](#entidades)
  - [Repositorios](#repositorios)
  - [Servicios](#servicios)
  - [Controladores REST](#controladores-rest)
  - [Seguridad (JWT)](#seguridad-jwt)
- [Frontend (Android Kotlin)](#frontend-android-kotlin)
  - [Pantallas](#pantallas)
  - [Adaptadores y Modelos](#adaptadores-y-modelos)
  - [Integración con API](#integración-con-api)
- [Base de Datos](#base-de-datos)
- [Capturas](#capturas)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Autor](#autor)

---

## Tecnologías Usadas

### Backend:

- Java 23
- Spring Boot
- Spring Security + JWT
- JPA/Hibernate
- MySQL
- Maven

### Frontend:

- Kotlin (Android)
- Material Components
- Retrofit + OkHttp

---

## Estructura del Proyecto

```
transportes/
├── backend/ (Spring Boot)
└── frontend/ (Android Kotlin)
└── base de datos/ (MySQL)
```

---

## Backend (Spring Boot)

### Entidades:

- `Marca`: id, nombre, descripcion, imagen, anioFundacion, List<Modelo>
- `Modelo`: id, nombre, descripcion, precioBase, color, marca
- `Color`: id, descripcion, precio
- `Usuario`: para autenticación y roles

### Repositorios:

Interfaces JPA para `Marca`, `Modelo`, `Color`, `Usuario`.

### Servicios:

Clases `@Service` que contienen la lógica de negocio. Validaciones, asociaciones, etc.

### Controladores REST:

- `/api/marcas` (GET, POST, PUT, DELETE)
- `/api/modelos` (GET, POST, PUT, DELETE)
- `/api/colores` (GET)
- `/api/auth` para login con JWT

### Seguridad (JWT):

- Autenticación con username/password
- Token JWT en cabecera Authorization
- Seguridad de endpoints con roles

---

## Frontend (Android Kotlin)

### Pantallas:

- LoginActivity
- RegistroActivity
- MarcaActivity (Crear, editar, consultar marca y sus modelos)
- ModeloActivity (Crear, editar, consultar modelo)

### Adaptadores y Modelos:

- `ModeloAdapter`, `MarcaAdapter`
- Clases `data class` para reflejar la estructura de las entidades del backend

### Integración con API:

- `ApiClient`: configuración base OkHttp
- `MarcasClient`, `ModelosClient`, `ColoresClient`: llamadas REST
- Autenticación mediante SharedPreferences y token JWT

---

## Base de Datos

MySQL con las siguientes tablas:

- marcas
- modelos
- colores
- usuarios

Relaciones:

- Una `marca` tiene muchos `modelos`
- Cada `modelo` tiene un `color`

---

## Capturas

> Se pueden incluir capturas de la app mostrando:
>
> - Pantalla de login
> - Listado de marcas
> - Edición de modelo

---

## Instalación y Ejecución

### Backend:

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend:

- Abrir en Android Studio
- Ejecutar en emulador o dispositivo

### Configuración:

- Configurar en `ApiClient.kt` la URL base de la API REST

---

## Autor

Alex Moreno - 2025

> Proyecto desarrollado como ejercicio completo de aplicación cliente-servidor REST con Android y Spring Boot.
