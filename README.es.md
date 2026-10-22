# API de Reservas de Canchas Deportivas

[English](README.md) \| [Español](README.es.md)

Esta es una API REST para gestionar reservas de canchas deportivas,
usuarios y pagos. Desarrollé el proyecto para practicar la creación de
un backend con Spring Boot, trabajando con autenticación, permisos por
rol, validaciones, persistencia y pruebas automatizadas.

La API contempla cuatro tipos de cancha: fútbol, baloncesto, tenis y
pádel. También aplica reglas para evitar reservas que se solapen y
controla el acceso para que cada usuario pueda gestionar sus propias
reservas y pagos, mientras que los administradores tienen permisos más
amplios.

## Funcionalidades

-   Registro de usuarios e inicio de sesión mediante JWT.
-   Control de acceso por roles: `USER` y `ADMIN`.
-   Operaciones CRUD para usuarios, canchas, reservas y pagos, según los
    permisos de cada recurso.
-   Paginación y ordenamiento en los endpoints de consulta.
-   Validaciones de fechas, horarios, disponibilidad de canchas y
    reservas solapadas.
-   Cálculo automático del importe del pago según la duración de la
    reserva y el precio por hora de la cancha.
-   Manejo centralizado de errores.
-   Documentación de la API con Swagger UI.
-   Pruebas automatizadas con JUnit y Mockito.

## Tecnologías

-   Java
-   Spring Boot
-   Spring Web MVC
-   Spring Data JPA e Hibernate
-   Spring Security
-   JSON Web Tokens (JJWT)
-   MySQL
-   Maven
-   Springdoc OpenAPI / Swagger UI
-   JUnit y Mockito

## Requisitos

Antes de ejecutar la aplicación, necesitas:

-   JDK 17 o una versión superior.
-   MySQL instalado y en ejecución, o acceso a un servidor MySQL.
-   Git, si vas a clonar el repositorio.

El proyecto declara Java 17 en la configuración de Maven. Durante el
desarrollo y las pruebas locales utilicé Java 21.

## Configuración

Crea una base de datos de MySQL llamada `apidereservas`:

``` sql
CREATE DATABASE apidereservas;
```

La aplicación obtiene la contraseña de MySQL y el secreto para firmar
los JWT desde variables de entorno. Configúralas antes de iniciar el
proyecto y evita subir credenciales o secretos reales al repositorio.

En PowerShell:

``` powershell
$env:DB_PASSWORD="tu_contraseña_mysql"
$env:JWT_SECRET="tu_secreto_codificado_en_base64"
```

`JWT_SECRET` debe ser un secreto fuerte codificado en Base64 y adecuado
para firmar JWT. Los tokens tienen una duración configurada de
86.400.000 milisegundos (24 horas).

La configuración local actual utiliza:

-   Base de datos: `apidereservas`
-   URL: `jdbc:mysql://localhost:3306/apidereservas`
-   Usuario: `root`
-   Gestión del esquema: Hibernate `ddl-auto=update`

Si tu instalación de MySQL utiliza otro host, puerto, nombre de base de
datos o usuario, revisa `src/main/resources/application.properties`.

## Ejecutar la aplicación

Desde la carpeta raíz del proyecto, ejecuta el Maven Wrapper:

``` powershell
.\mvnw spring-boot:run
```

También puedes ejecutar la clase principal de la aplicación desde tu
IDE.

Cuando la aplicación esté iniciada, la URL local predeterminada será
`http://localhost:8080`.

## Documentación de la API

Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

Documento OpenAPI:

`http://localhost:8080/v3/api-docs`

Inicia sesión para obtener un JWT. Después, en Swagger UI, pulsa
**Authorize** e introduce el token como Bearer token para probar los
endpoints protegidos.

## Rutas principales

  -----------------------------------------------------------------------
  Recurso                 Ruta base               Propósito
  ----------------------- ----------------------- -----------------------
  Autenticación           `/api/auth`             Iniciar sesión y
                                                  obtener un JWT

  Usuarios                `/api/users`            Registrar y gestionar
                                                  cuentas de usuario

  Canchas                 `/api/courts`           Consultar y gestionar
                                                  canchas deportivas

  Reservas                `/api/reservations`     Crear y gestionar
                                                  reservas de canchas

  Pagos                   `/api/payments`         Crear y gestionar pagos
                                                  de reservas
  -----------------------------------------------------------------------

Las operaciones disponibles en cada ruta dependen del endpoint y del rol
del usuario autenticado.

## Reglas de reservas y pagos

-   La reserva debe corresponder a una cancha existente y activa.
-   La hora de finalización debe ser posterior a la hora de inicio.
-   No se permiten reservas para fechas pasadas ni para horarios que ya
    hayan pasado durante el día actual.
-   Dos reservas activas no pueden solaparse en la misma cancha y fecha.
    Una reserva cancelada no bloquea el horario.
-   Los usuarios normales pueden acceder a sus propias reservas y a los
    pagos relacionados. Los administradores pueden consultar registros
    de distintos usuarios.
-   Cada reserva puede tener como máximo un pago.
-   No se puede crear un pago para una reserva cancelada.
-   El importe se calcula a partir de la duración de la reserva y del
    precio por hora de la cancha. Al actualizar un pago, solo se
    modifica el método de pago; no se recalculan el importe ni el
    estado.

## Seguridad y manejo de errores

La API centraliza el manejo de excepciones y devuelve respuestas de
error estructuradas. Según el caso, puede responder con
`400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`
o `500 Internal Server Error`.

Las contraseñas se almacenan usando BCrypt y las solicitudes protegidas
se autentican mediante JWT. Los permisos distinguen entre usuarios
normales y administradores; en reservas y pagos también se verifica la
propiedad del recurso.

## Pruebas

Durante la validación del proyecto, la suite de pruebas terminó
correctamente:

``` text
Tests run: 61, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Puedes ejecutar las pruebas con:

``` powershell
.\mvnw clean test
```

## Estado del proyecto

La API principal, el flujo de seguridad, la documentación de Swagger y
las pruebas automatizadas están implementados y validados. Este proyecto
forma parte de mi práctica continua para desarrollar aplicaciones
backend con Java, reglas de negocio realistas y control de acceso.
