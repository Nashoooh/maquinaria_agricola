# 🌾 Proyecto Agricola - Grupo 11

Aplicación web para gestión de maquinaria agrícola. Permite a los usuarios registrarse, crear perfiles, publicar avisos, reservar maquinaria y consultar información agrícola relevante.  
Desarrollada con **Spring Boot, Spring Security, Thymeleaf y MySQL**.

---

## 🛠 Tecnologías utilizadas

- **Backend:** Spring Boot 3, Spring Security, Thymeleaf  
- **Base de datos:** MySQL 8  
- **Frontend:** HTML, CSS, Thymeleaf  
- **Contenerización:** Docker  
- **Java:** 17  

---

## ⚙ Requisitos

- Java 17
- Maven
- Docker
- MySQL accesible desde la aplicación

---

## 🔧 Configuración del proyecto

- Crear un archivo `.env` en la raíz del proyecto con las credenciales de la base de datos:
- El archivo .env está en .gitignore para proteger las credenciales, este se envia por separado

---

## 🐳 Construcción y ejecución con Docker

- docker build -t agricola-grupo11 .
- docker run -p 8080:8080 --env-file .env agricola-grupo11
http://localhost:8080

---

## 🔓 Páginas públicas

- /inicio	Información sobre agricultura, eventos, fechas de cosechas, publicidad y avisos.
- /registro	Formulario de registro de usuarios.
- /login	Formulario de inicio de sesión.
- /maquinaria/buscar	Búsqueda de maquinaria disponible.

## 🔒 Páginas privadas (requieren autenticación)

- /home	Panel principal del usuario con enlaces a funcionalidades privadas.
- /perfil	Gestión del perfil del usuario (dirección, teléfono, cultivos).
- /maquinaria/avisos	Publicar avisos de arriendo de maquinaria.
- /maquinaria/reserva	Reservar maquinaria disponible.
- /maquinaria/detalle/{id}	Visualización de detalles de una maquinaria específica.

---

## 📦 Comandos útiles

- Construir JAR
mvn clean package -DskipTests

- Reiniciar contenedor
docker stop <nombre_contenedor>
docker rm <nombre_contenedor>
docker run -p 8080:8080 --env-file .env agricola-grupo11
