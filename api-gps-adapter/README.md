# api-gps-adapter
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)](https://api-internal.dev.apps.menta.global/gps/swagger-ui/index.html)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](postman)

**Description**: api que se encarga de procesar las solicitudes de autorización de pagos, devoluciones, reembolsos y reversos del adquirente Global Processing.


## Index:
- [Stack](#stack)
- [How to Run](#how-to-run)
    - [Config](#config)
    - [Build](#build)
    - [Run](#run)
    - [Unit Tests](#unit-tests)
    - [Linter](#linter)
    - [Sonar](#sonarqube)
- [Contributing](#contributing)

### Stack 🛠️
- java 11
- kotlin 1.6
- spring-boot 2.6
- [arrow-kt](https://arrow-kt.io/)
- [kotest](https://kotest.io/)
- [jpos](http://www.jpos.org/)

### How to Run

#### Config

- ##### springdoc

  Esto se usa para definir el path en el que se expondra el swagger del servicio

  ```yaml
  springdoc:
    api-docs:
      path: ${APP_PATH}/swagger-ui/api-docs
    swagger-ui:
      path: ${APP_PATH}/index.html
  ```

  En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `-DAPP_PATH=/gps`

#### Build

Ejecuta éste comando para instalar las dependencias y buildear el proyecto:

```bash
$ ./gradlew build
```

#### Run

Para correr el proyecto:

```bash
$ ./gradlew bootRun
```

El entorno de desarrollo corre sobre <http://localhost:8080>. Ejecuta una llamada GET de prueba en <http://localhost:8080/actuator>

#### Unit Tests

Para correr los tests unitarios
```bash
$ ./gradlew test
```

#### Linter
- ##### Check
  Para comprobar
  ```bash
  $ ./gradlew ktlintCheck
  ```
- ##### Format
  Para formatear el código
  ```bash
  $ ./gradlew ktlintFormat
  ```

#### Sonarqube

Para correr sonarqube localmente. Para mas información acerca de la configuración dirigirse al siguiente [link](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/16777217/Sonarqube+local)
```bash
$ ./gradlew sonarqube \
-Dsonar.projectKey=<PROJECT-KEY> \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=<TOKEN-SONAR-PROJECT>
```

### Contributing
Leer [Material Técnico](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/3965199/Material+T+cnico), sobre todo la seccion sobre [Arquitectura Hexagonal](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/4423684/Arquitectura+Hexagonal)



