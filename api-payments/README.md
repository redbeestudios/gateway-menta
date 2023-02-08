# api-payments
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)](https://api-internal.dev.apps.menta.global/payments/swagger-ui/index.html)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](postman)

**Description**: api que se encarga de procesar las solicitudes de autorización de pagos que realiza el merchant, mediante el POS, y las envía al adquirente para su aprobación o rechazo.


## Index:
- [Stack](#stack)
- [How to Run](#how-to-run)
    - [Config](#config)
    - [Build](#build)
    - [Run](#run)
    - [Unit Tests](#unit-tests)
- [Contributing](#contributing)
- [Usage](#usage)

### Stack 🛠️
- java 11
- kotlin 1.6
- spring-boot 2.6
- [arrow-kt](https://arrow-kt.io/)
- [kotest](https://kotest.io/)
- [mariadb](https://mariadb.com/)
- [apache-kafka](https://kafka.apache.org/)

### How to Run

#### Prerequisites

[_docker-local-environment_](https://git.menta.global/backend/utils/docker-local-environment)
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

  En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `-DAPP_PATH=/payments`

- ##### datasource
  Esto se usa para definir la conexión a la base de datos
  ```yaml
  datasource:
    url: jdbc:mariadb://${MYSQL_HOST}:3306/${MYSQL_DB}
    username: ${MYSQL_USER}
    password: ${MYSQL_PASS}
  ```

  En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar

  `-DMYSQL_HOST=localhost`
  `-DMYSQL_DB=db-payments`
  `-DMYSQL_USER=root`
  `-DMYSQL_PASS=root`

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


