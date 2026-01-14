# 🔨 Auction Platform – Microservices Architecture

Plataforma de **subastas online** desarrollada con **arquitectura de microservicios**, que permite gestionar usuarios, autenticación, subastas, pujas, pagos y notificaciones, con un frontend web desacoplado.

---

## 🚀 Tecnologías

### Backend

* **Spring Boot**
* **Spring Security + JWT**
* **PostgreSQL**
* **Mercado Pago API** (pagos y depósitos de garantía)

### Frontend

* **Angular**

---

## 🧩 Arquitectura del Sistema

El sistema está dividido en los siguientes módulos / microservicios:

* **User Service** → Gestión de usuarios, Autenticación, registro, verificación y recuperación de contraseña
* **Subastas Service** → Gestión de subastas. Gestión de pujas y pujas automáticas
* **Payment Service** → Pagos y depósitos de garantía (Mercado Pago)
* **Notification Service** → Envío de notificaciones y correos
* **Frontend (Angular)** → Interfaz de usuario

Cada microservicio maneja su propia lógica de negocio y persistencia en **PostgreSQL**.

---

## 🔐 Seguridad y Autenticación

* Autenticación basada en **JWT**
* Spring Security integrado
* Endpoints protegidos según rol / estado del usuario
* Verificación de cuenta por email
* Recuperación de contraseña con token

---

## 📌 Endpoints Principales

### 👤 User Service (`/user`)

* `GET /user/users` → Listar usuarios
* `GET /user/{id}` → Obtener usuario por ID
* `PUT /user/editar/{id}` → Editar usuario
* `GET /user/me` → Usuario autenticado
* `GET /user/darDeBaja?email=` → Dar de baja usuario

---

### 🔑 Auth Service (`/auth`)

* `POST /auth/login` → Login (JWT)
* `POST /auth/register` → Registro de usuario
* `GET /auth/verify?token=` → Verificación de cuenta
* `POST /auth/activar?email=` → Activar cuenta
* `POST /auth/recover` → Solicitar recuperación de contraseña
* `POST /auth/reset-password` → Restablecer contraseña

---

### 🏷️ Subastas (`/subastas`)

* `POST /subastas/postSubasta` → Crear subasta
* `GET /subastas/activas` → Subastas activas
* `GET /subastas/getAllSubastas` → Todas las subastas
* `GET /subastas/subasta/{id}` → Subasta por ID
* `GET /subastas/estado/{estado}` → Subastas por estado
* `GET /subastas/getSubastas/{estado}/{categoria}` → Subastas por categoría
* `PUT /subastas/{id}` → Actualizar subasta
* `PUT /subastas/ocultar/{id}` → Dar de baja subasta
* `PUT /subastas/{subastaId}/ganador/{ganadorId}` → Asignar ganador

---

### 💰 Pujas (`/pujas`)

* `POST /pujas/postPuja` → Crear puja
* `POST /pujas/puja-automatica` → Activar puja automática
* `GET /pujas/getPujas` → Listar pujas
* `GET /pujas/puja/{subastaId}` → Puja más alta
* `GET /pujas/getPujas/subastaId/{subastaId}` → Pujas por subasta
* `GET /pujas/pujasUserId/{userId}` → Pujas por usuario
* `DELETE /pujas/deletePuja/{id}` → Eliminar puja

---

### 💳 Pagos (`/pagos`)

* `POST /pagos/crear-preferencia` → Crear pago de subasta
* `POST /pagos/crear-deposito` → Crear depósito de garantía
* `POST /pagos/webhook` → Webhook Mercado Pago
* `GET /pagos/estado/{subastaId}` → Estado del pago
* `GET /pagos/deposito/{subastaId}/{userId}` → Estado del depósito
* `GET /pagos/{usuarioId}` → Pagos del usuario

---

### 🔔 Notificaciones (`/notification`)

* `POST /notification/email/ganador/{email}` → Email ganador
* `POST /notification/email/subasta-creada/{email}` → Email subasta creada
* `POST /notification/verification/email/{email}` → Email verificación
* `POST /notification/forgot-password/email/{email}` → Email recuperación
* `POST /notification/finalizada/email/{email}` → Email pago realizado
* `GET /notification/{userId}` → Notificaciones del usuario
* `PUT /notification/{id}/ocultar` → Ocultar notificación

---

### 📊 Informes (`/api/informe`)

* `GET /api/informe/vendedores` → Informe por vendedor
* `GET /api/informe/pujas-por-usuario` → Reporte de pujas

---

## 🖥️ Frontend (Angular)

* Registro y login de usuarios
* Verificación de cuenta por email
* Listado y filtrado de subastas
* Pujas manuales y automáticas
* Pagos y depósitos de garantía
* Historial de pujas y subastas
* Notificaciones en tiempo real

---

## 👤 Autor

Desarrollado por **Nicolás Perlo**

---

## 📄 Licencia

Proyecto de uso educativo / demostrativo.
